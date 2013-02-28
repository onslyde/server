
function registerMember(memberData) {
    //clear existing  msgs
    $('span.invalid').remove();
    $('#success-legend').remove();
    disableForm();
    $.ajax({
        url: 'go/members',
        contentType: "application/json",
        dataType: "json",
        type: "POST",
        data: JSON.stringify(memberData),
        success: function(data) {
            //clear input fields
//            $('#reg')[0].reset();

            $('#forminputs').remove();

            //mark success on the registration form
            $('#formMsgs').append($('<span class="alert span6 alert-success">Success!!</span>' +
              '<br style="clear:left"/><p id="success-legend">Your session ID is <strong>' + data.sessionId + '</strong><br/>You\'ll also get a confirmation email in case you forget it. (check spam if it doesn\'t show up).</p>'));
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


