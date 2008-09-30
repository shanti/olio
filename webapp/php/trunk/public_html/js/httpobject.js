                
function getHTTPObject() {
      var xmlhttp;
      if (!xmlhttp && typeof XMLHttpRequest != 'undefined') {
          try {
              xmlhttp = new XMLHttpRequest();
          } catch (e) {
              xmlhttp = false;
          }
      }
      return xmlhttp;
}
var http = getHTTPObject();
