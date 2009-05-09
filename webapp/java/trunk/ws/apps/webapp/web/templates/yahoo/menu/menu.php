<?php
  addWidget( array( "name" =>"yahoo.menu", 
	"value" => "{menu: [ 
              { label :'Action',
                action : { topic : '/foo', message : { value : 'test.jsp' } }
              },
              { label : 'Must Read',
              
                menu: [
                 { label:'dev.java.net',
                   menu: [ 
                     { label : 'jMaki',
                       href :'http://ajax.dev.java.net'
                     },
                     { label : 'Glass',
                       href : 'http://glassfish.dev.java.net' 
                     }
                    ]  
                  }
                ]                             
              }, 
              { label:'Click me for fun!',style:{strongemphasis:true} },
              { label:'Disabled!',style:{disabled:true} },
              { label:'Yahoo!', 
                href :'http://www.yahoo.com' },
              { label:'Sun Microsystems',
                href: 'http://www.sun.com',style:{checked:true} },
              { label:'Oracle', 
                href: 'http://www.oracle.com' }
              ]}"
  ));
?>