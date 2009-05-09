<?php
    addWidget( array("name" => "yahoo.tabbedview",
        "value" => "{items:[
        {label : 'My Tab', content : 'Some Content'},
        {id : 'bar', label : 'My Tab 2', include : 'test.jsp ', lazyLoad : true },
        {label : 'My Tab 3', content : 'More Content',  selected : true} ] }"
    ));
?>
