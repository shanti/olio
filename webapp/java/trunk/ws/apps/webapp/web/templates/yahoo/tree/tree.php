<?php
  addWidget( array ( "name" => "yahoo.tree",
     "value" =>"{
        root : {
           label : 'Yahoo Tree Root Node',
           expanded : true,
           children : [
             { label : 'Node 1.1'},
             { label : 'Node 1.2',
                 children : [
                   { label : 'Node 3.1',
                     action : { topic : '/foo/select', message : {targetId : 'bar'}}
                   }
                 ]
             }
            ]
          }}" 
  )); 
?>
