
function registerMember(memberData) {
    //clear existing  msgs
    $('span.invalid').remove();
    $('#success-legend').remove();
    disableForm();
    $.ajax({
        url: '/go/members',
        contentType: "application/json",
        dataType: "json",
        type: "POST",
        data: JSON.stringify(memberData),
        success: function(data) {
            //clear input fields
//            $('#reg')[0].reset();

            $('#forminputs').remove();

            //mark success on the registration form
            $('#signupmodal').toggleClass('success');
            $('#signupmodal .modal-header').html('<h3>Success!!!</h3>');
            $('#formMsgs').append($('<p id="success-legend"><br><span style="font-size: 32px;">Your session ID is: <strong class="alert alert-success" style="font-size: 32px;">' + data.sessionId + '</strong></span>' +
              '<br style="clear:left"/><br/>' +
              'Read the <a href="#getstarted" data-toggle="modal">Getting Started</a> section below for details on setting up.' +
              '</p>'));
            $('#signupmodal .modal-footer').html('<a href="#signupmodal" role="button" class="btn pull-right" data-toggle="modal">Continue</a>');
        },
        error: function(error) {
            if ((error.status == 409) || (error.status == 400)) {
                //console.log("Validation error registering user!");

                var errorMsg = $.parseJSON(error.responseText);

                $.each(errorMsg, function(index, val) {
                    $('<span class="invalid">' + val + '</span>').insertAfter($('#' + index));
                });
            } else {
                //console.log("error - unknown server issue");
                $('#formMsgs').append($('<span class="invalid">Unknown server error</span>'));
            }
        },
        complete: function(complete) {
          enableForm();
        }
    });

  function disableForm(){
    $("#email").attr("disabled", "disabled");
    $("#fullname").attr("disabled", "disabled");
    $("#password").attr("disabled", "disabled");
    $("#register").attr("disabled", "disabled");
  }

  function enableForm(){
    $("#email").removeAttr("disabled");
    $("#fullname").removeAttr("disabled");
    $("#password").removeAttr("disabled");
    $("#register").removeAttr("disabled");
  }
}


