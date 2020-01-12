package me.mugon.todolist.resolver;

import lombok.RequiredArgsConstructor;
import me.mugon.todolist.annotation.CurrentUser;
import me.mugon.todolist.domain.Account;
import me.mugon.todolist.domain.adapter.AccountAdapter;
import me.mugon.todolist.domain.enums.SocialType;
import me.mugon.todolist.repository.AccountRepository;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;

import static me.mugon.todolist.domain.enums.SocialType.*;

@Component
@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final AccountRepository accountRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null && parameter.getParameterType().equals(Account.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        Account account = (Account) session.getAttribute("account");
        return getAccount(account, session);
    }

    private Account getAccount(Account account, HttpSession session) {
        if (account == null) {
            try {
                OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

                Map<String, Object> map = authentication.getPrincipal().getAttributes();
                Account convertAccount = convertAccount(authentication.getAuthorizedClientRegistrationId(), map);
                assert convertAccount != null;
                account = accountRepository.findByEmail(convertAccount.getEmail()).orElseGet(() -> accountRepository.save(convertAccount));

                selfRoleIfNotSame(account, authentication, map);
                session.setAttribute("account", account);
            } catch (ClassCastException e) {
                return getIdPasswordAccount();
            }
        }
        return account;
    }

    private Account getIdPasswordAccount() {
        AccountAdapter idPasswordAccount = (AccountAdapter) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return idPasswordAccount.getAccount();
    }

    private void selfRoleIfNotSame(Account account, OAuth2AuthenticationToken authentication, Map<String, Object> map) {
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority(account.getSocialType().getRoleType()))) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(map, "N/A", AuthorityUtils.createAuthorityList(account.getSocialType().getRoleType())));
        }
    }

    private Account convertAccount(String authority, Map<String, Object> map) {
        if (FACEBOOK.isEquals(authority)) return getModernUser(FACEBOOK, map);
        if (GOOGLE.isEquals(authority)) return getModernUser(GOOGLE, map);
        if (KAKAO.isEquals(authority)) return getKaKaoAccount(map);
        return null;
    }

    private Account getModernUser(SocialType socialType, Map<String, Object> map) {
        return Account.builder()
                .email(String.valueOf(map.get("email")))
                .principal(String.valueOf(map.get("id")))
                .socialType(socialType)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Account getKaKaoAccount(Map<String, Object> map) {
        return Account.builder()
                .principal(String.valueOf(map.get("id")))
                .socialType(KAKAO)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
