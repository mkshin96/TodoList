$('.email').blur(function () {
    if($('.email').val().trim().length === 0){
        $('.emailErrorMessage').html("<p style='color: #DE5E52; font-size: 12px; margin-top: -3px'>필수항목입니다.</p>").show()
        return false;
    }
});

$('.password').blur(function () {
    if($('.password').val().trim().length === 0){
        $('.passwordErrorMessage').html("<p style='color: #DE5E52; font-size: 12px; margin-top: -3px'>필수항목입니다.</p>").show();
        return false;
    }
});

$('.btn_social').click(function () {
    var socialType = $(this).data('social');
    location.href="/oauth2/authorization/"+socialType;
});