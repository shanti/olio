<?php  addWidget( array("name" => "jmaki.accordionMenu",
    "value" => "{ menu : [
       {label: 'Links',
            menu: [
                { label : 'Sun.com',
                  href : 'http://www.sun.com'},
                { label : 'jMaki.com',
                  href : 'http://www.jmaki.com'}
                ]
       },
       {label: 'Actions',
            menu: [
                { label : 'Select',
                  action :{topic: '/foo/select',
                         message: { targetId : 'bar'}}
                },
                { label :'Set Content',
                  action :{topic: '/foo/setContent',
                         message: { value : 'test.jsp'}}
                }
                ]}
                ]}"
        ));
?>