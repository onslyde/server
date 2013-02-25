
function registerMember(memberData) {
    //clear existing  msgs
   // $('span.invalid').remove();
    //$('span.success').remove();

    $.ajax({
        url: 'go/members',
        contentType: "application/json",
        dataType: "json",
        type: "POST",
        data: JSON.stringify(memberData),
        success: function(data) {
            console.log("Member registered");

            //clear input fields
            $('#reg')[0].reset();

            //mark success on the registration form
            $('#formMsgs').append($('<legend><span class="alert alert-success">Success!!</span><br/>Make a note of this! Your session ID is <strong>' + data.sessionId + '</strong></legend>'));

            //updateMemberTable();
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
        }
    });
}


