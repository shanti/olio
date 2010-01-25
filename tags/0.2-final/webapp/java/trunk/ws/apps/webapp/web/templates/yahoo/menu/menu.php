/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
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