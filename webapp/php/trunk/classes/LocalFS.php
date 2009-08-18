<?php
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
    
/**
 * Class for accessing the local file system.
 * Allows creation, retrieval and storage of files, querying
 * existence of a file, etc.
 */
//require_once("../classes/DirectoryType.php");
class LocalFS extends FileSystem {
	
	function __construct() {
		$this->localFSRoot = Olio::$config['localfsRoot'];
	}
	
	function getRootDir() {
		return $this->localFSRoot;
	}

    /*
     * This method creates the path that is needed for the file that is to be created/copied
     * It takes a directoryType and an id.  It then generates the
     * path and if it does not already exist it creates it. If it is unable to create the path
     * it returns false, else it returns true.
     */

    function createPath($dirType, $identifier)
    {
        //build path variables
        $dirPrimaryPath = sprintf("%03d", $identifier % 1000);
        $dirSecondaryPath = sprintf("%03d", $identifier / 1000000 % 1000);
        $base_directory = $this->localFSRoot;

        if($dirType != "invalid")
        {

            $path = $base_directory . '/' . $dirType . '/' . $dirPrimaryPath . '/' . $dirSecondaryPath;
            if(is_dir($path) == true)
            {
			return true;
            }else
            {
                if(is_dir($base_directory . '/' . $dirType . '/' . $dirPrimaryPath) == true)
                {
                    //partial path exists eg /filestore/people/444 initial hash dir exists
                    //so we need to complete the path

                    if(mkdir($base_directory . '/' . $dirType . '/' . $dirPrimaryPath . '/' . $dirSecondaryPath, 0777, true) == true)
                    {
                        return true;
                    }else //mkdir($base_directory . '/' . $dirTypeExpanded . '/' . $dirPrimaryPath . '/' . $dirSecondaryPath, 0777, true) failed
                    {
                        return false;
		}
		

                }else if(is_dir($base_directory . '/' . $dirType) == true)
                {
                    //partial path exists eg /filestore/people initial hash dir does not exists
                    //so we need to complete the path
                    if(mkdir($base_directory . '/' . $dirType . '/' . $dirPrimaryPath, 0777, true) == true)
                    {
                        if(mkdir($base_directory . '/' . $dirType . '/' . $dirPrimaryPath . '/' . $dirSecondaryPath, 0777, true) == true)
                        {
                            return true;
                        }else //mkdir($base_directory . '/' . $dirType . '/' . $dirPrimaryPath . '/' . $dirSecondaryPath, 0777, true) failed
                        {
				return false;
			}
                    }else //mkdir($base_directory . '/' . $dirType . '/' . $dirPrimaryPath, 0777, true) failed
                    {
                        return false;
                    }
			
                }else //the path really is not there so it needs to be created
                {
                    if(mkdir($base_directory . '/' . $dirType, 0777, true) == true)
                    {

                        if(mkdir($base_directory . '/' . $dirType . '/' . $dirPrimaryPath, 0777, true) == true)
                        {
                            if(mkdir($base_directory . '/' . $dirType . '/' . $dirPrimaryPath . '/' . $dirSecondaryPath, 0777, true) == true)
                            {
                                return true;
                            }else //mkdir($base_directory . '/' . $dirType . '/' . $dirPrimaryPath . '/' . $dirSecondaryPath, 0777, true) failed
                            {
                                return false;
		}		
                        }else //mkdir($base_directory . '/' . $dirType . '/' . $dirPrimaryPath, 0777, true) failed
                        {
                            return false;
	}
                    }else //mkdir($base_directory . '/' . $dirType, 0777, true) failed
                    {
                        printf("mkdir failed");
                        return false;
                    }
                }//end else path does not exist
            }//end else whole path does not exist
	
        }else //DirectoryType::Decode() returned invalid
        {
            return false;
        }
    }

    static $typemap = array("p" => "person", "P" => "person",
                            "e" => "event", "E"=> "event");

    function mapAttributes($filename) {
        // Do pattern matching and splitting.
        $prefix = substr($filename, 0, 1);
        $attrs["type"] = LocalFS::$typemap[$prefix];
        $dotidx = strrpos($filename, ".");
        $postfix = substr($filename, $dotidx - 1, 1);
        if (is_numeric($postfix)) {
            $attrs["id"] = substr($filename, 1, $dotidx - 1);
            $attrs["type"] .= "s";
        } else if ($postfix == "t" || $postfix =="T") {
            $attrs["id"] = substr($filename, 1, $dotidx - 2);
            $attrs["type"] .= "Thumbs";
        } else if ($postfix == "l" || $postfix =="L") {
            $attrs["id"] = substr($filename, 1, $dotidx - 2);
            $attrs["type"] .= "Lits";
        } else {
            error_log("Invalid file name pattern ".$filename);
            $attrs["type"] = "invalid";
        }
        return $attrs;
    }

    function create($srcpath, $replication_factor='1',	$overwrite = 'true') {
        
        $filename = basename($srcpath); //remove the file's path so we can work with just the filename.
        $attrs = $this->mapAttributes($filename);
	    $destpath = $this->getFullPath($filename, $attrs);

        if ($destpath == "invalid") {
            printf("Invalid response from getFullPath");
            return false;
        } else {
            if ($overwrite) {
                //printf("<p>Using overwrite path</p>");
                if ($this->createPath($attrs["type"], $attrs["id"])) {
                    //we are assuming that the file will be stored in /tmp
                    return copy($srcpath, $destpath);
                } else {//path cretion failed
                    return false;
                }
            } else {//we are not allowing overwrite
                //printf("<p>Using non-overwrite path</p>");
                if (file_exists($destpath)) {
                    return false;
                } else {  //file does not exist
                    if ($this->createPath($attrs["type"], $attrs["id"]) == true) {
                        //we are assuming that the file will be stored in /tmp
                        return copy("/tmp/" . $filename, $destpath);
                    } else {//path cretion failed
                        return false;
                    }
                } //end file_exists else
            } //end overwrite else
        } //end invalid else
	}

    
	
	/*function getNewFileName($oldfilename) {
		return $this->localFSRoot . '/' . basename($oldfilename);
	}*/

    function getFullPath($filename, $attrs = null) {
        if (is_null($attrs)) {
            $attrs = $this->mapAttributes($filename);
        }

        //build path
        $dirPrimaryPath = sprintf("%03d", $attrs["id"] % 1000);
        $dirSecondaryPath = sprintf("%03d", $attrs["id"] / 1000000 % 1000);

        $base_directory = $this->localFSRoot;

        if($attrs["type"] != "invalid") {
            $path = $base_directory . '/' . $attrs["type"] . '/' . $dirPrimaryPath . '/' . $dirSecondaryPath . '/' . $filename;
            return $path;
        } else {
            //return invalid as a failed message
            return "invalid";
	}
    }
	
	
	function getPaths($filename) {
	   return array(0 => $this->getFullPath($filename));
	}

	/* return true or false */
	function delete($filename) { return unlink($this->getFullPath($filename)); }

	/* return true or false */
	function exists($filename) { return file_exists($this->getFullPath($filename)); }

	function open($filename) { return file_get_contents($this->getFullPath($filename)); }
}
?>
