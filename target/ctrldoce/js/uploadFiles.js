$(function() {
    // Variable to store your files
    var files = new Array();
    var x = 0;
    // Add events
    //$('input[type=file]').on('change', prepareUpload);
    //$('form').on('submit', uploadFiles);

    $("#enviar").button().click(function(event){
        event.preventDefault();
        uploadFiles(event);
        
    });

    var cont = 1;
    $("#adRow").button().click(function(event) {
        event.preventDefault();
        var newTxt = $('<li><label for="gasto_xml_'+cont+'">XML </label><input type="file" name="gasto_xml_'+cont+'" class="inputfilechange" id="gasto_xml_'+cont+'" multiple><br /><label for="gasto_pdf_'+cont+'">PDF </label><input type="file" name="gasto_pdf_'+cont+'" class="inputfilechange" id="gasto_pdf_'+cont+'" multiple></li>');
        $("#a1").append(newTxt);
        cont++;
        //$(".inputfilechange").on('change', function(e){prepareUpload(e)} );
        $(".inputfilechange").on('change', prepareUpload);
    });
    // Grab the files and set them to our variable
    $(".inputfilechange").on('change', prepareUpload);

    function prepareUpload(event) {
        files[x++] = event.target.files;                
    }

    // Catch the form submit and upload the files
    function uploadFiles(event) {
        //console.info(event);        
        event.stopPropagation(); // Stop stuff happening
        event.preventDefault(); // Totally stop stuff happening

        // START A LOADING SPINNER HERE

        // Create a formdata object and add the files
        //var data = new FormData();
        var data = new FormData($("#FORM_LOAD_FILES")[0]); 
        //$.each(files, function(key, value) {
        //    data.append(key, value);            
        //});

        console.info(data);

        $.ajax({
            url: '../ws/files/upload.do?files',
            type: 'POST',
            data: data,
            cache: false,
            processData: false, // Don't process the files
            contentType: false, // Set content type to false as jQuery will tell the server its a query string request
            success: function(data, textStatus, jqXHR) {
                if (typeof data.error === 'undefined') {
                    // Success so call function to process the form
                    console.log("DATA: " + data);
                    submitForm(event, data);
                } else {
                    // Handle errors here
                    console.log('ERRORS: ' + data.error);
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                // Handle errors here
                console.log('ERRORS: ' + textStatus);
                // STOP LOADING SPINNER
            }
        });
    }

    function submitForm(event, data) {
        // Create a jQuery object from the form
        $form = $(event.target);

        // Serialize the form data
        var formData = $form.serialize();

        // You should sterilise the file names
        $.each(data.files, function(key, value) {
            formData = formData + '&filenames[]=' + value;
        });

        $.ajax({
            url: 'submit.php',
            type: 'POST',
            data: formData,
            cache: false,
            dataType: 'json',
            success: function(data, textStatus, jqXHR)
            {
                if (typeof data.error === 'undefined') {
                    // Success so call function to process the form
                    console.log('SUCCESS: ' + data.success);
                } else {
                    // Handle errors here
                    console.log('ERRORS: ' + data.error);
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                // Handle errors here
                console.log('ERRORS: ' + textStatus);
            },
            complete: function() {
                // STOP LOADING SPINNER
            }
        });
    }
    
    
});

