var duplicatedPass = false;
var passwordPass = true;
var emailPass = true;

$('.register').click(function () {
    var email = '.email';
    var password = '.password';

    if(duplicatedPass === false){
        alert("중복된 email입니다.");
        $(email).focus();
        return false;
    }

    if(emailPass === false){
        alert("email을 확인해주세요.");
        $(email).focus();
        return false;
    }

    if(passwordPass === false){
        alert("password를 확인해주세요.");
        $(password).focus();
        return false;
    }

    if($(password).val().trim().length === 0){
        alert("PASSWORD를 입력하세요.");
        $(password).focus();
        return false;
    }

    if($(email).val().trim().length === 0){
        alert("email을 입력하세요.");
        $(email).focus();
        return false;
    }

    var jsonData = JSON.stringify({
        password : $(password).val(),
        email : $(email).val()
    });
    $.ajax({
        url: "http://localhost:8080/users",
        type: "POST",
        data: jsonData,
        contentType: "application/json",
        dataType: "json",
        success: function () {
            alert('등록 성공!');
            location.href = '/login';
        },
        error: function () {
            alert("등록 실패!");
        }
    });
});

$('.email').blur(function () {
    var email = '.email';
    var emailErrorMessage = '.emailErrorMessage';
    var getMail = RegExp(/^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/);
    var emailValue = $(email).val();

    if(emailValue.trim().length === 0){
        $(emailErrorMessage).html("<p style='color: #DE5E52; font-size: 12px; margin-top: -3px'>필수항목입니다.</p>").show();
        emailPass = false;
        return ;
    } else if(!getMail.test(emailValue)){
        $(emailErrorMessage).html("<p style='color: #DE5E52; font-size: 12px; margin-top: -3px'>이메일 형식이 맞지 않습니다.</p>").show();
        emailPass = false;
        return ;
    } else {
        $(emailErrorMessage).html("").hide();
        emailPass = true;
    }

    var emailData = JSON.stringify({
        email : emailValue
    });

    $.ajax({
        url: "http://localhost:8080/users/checkEmail",
        type: "POST",
        data: emailData,
        contentType: "application/json",
        dataType: "json",
        success: function () {
            duplicatedPass = true;
        },
        error:function (error) {
            duplicatedPass = false;
            var errText = JSON.parse(error.responseText);
            $(emailErrorMessage).html("<p style='color: #DE5E52; font-size: 12px; margin-top: -3px'>"+ errText.message +"</p>").show();
        }
    });
});

$('.password').blur(function () {
    var password = '.password';
    var passwordErrorMessage = '.passwordErrorMessage';
    var passwordValue = $(password).val();

    if(passwordValue.trim().length === 0){
        $(passwordErrorMessage).html("<p style='color: #DE5E52; font-size: 12px; margin-top: -3px'>필수항목입니다.</p>").show();
        passwordPass = false;
    } else if(passwordValue.trim().length <= 3 && passwordValue.trim().length > 0){
        $(passwordErrorMessage).html("<p style='color: #DE5E52; font-size: 12px; margin-top: -3px'>PASSWORD는 4자 이상이어야 합니다.").show();
        passwordPass = false;
    } else {
        $(passwordErrorMessage).html("").hide();
        passwordPass = true;
    }
});