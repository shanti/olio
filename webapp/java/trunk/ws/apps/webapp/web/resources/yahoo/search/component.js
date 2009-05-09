// define the namespaces
jmaki.namespace("jmaki.widgets.yahoo.search");

/**
 * Yahoo Search Widget
 *   This widget lets you search the Web using Yahoo Web Search and
 *   search for suggested spellings for certain words.
 *
 * @author Ahmad M. Zawawi <ahmad.zawawi@gmail.com>  
* @constructor
 * @see http://developer.yahoo.com/search/
 */
jmaki.widgets.yahoo.search.Widget = function(wargs) {
    
    var topic = "/yahoo/search";
    var uuid = wargs.uuid;
    var self = this;
    
    //shorthand for libs
    var Dom = YAHOO.util.Dom;
    
    
    /**
     * Suggest Spelling event name
     * @const
     */
    var ON_SUGGEST_SPELL = "/onSuggestSpell";
    
    /**
     * Search web event name
     * @const
     */
    var ON_SEARCH = "/onSearchWeb";
    
    // we run on the xhp now
    var service = jmaki.xhp;    
    
    var shown = true;
    
    //read configuration arguments
    if (typeof wargs.args != 'undefined') {
        //overide topic name if needed
        if(typeof wargs.args.topic != 'undefined') {
            topic = wargs.args.topic;
            jmaki.log("Yahoo search: widget uses deprecated topic. Use publish instead.");
        }
        if(typeof wargs.args.shown != 'hidden') {
            shown = wargs.args.shown;
        }
    }
    
    if (wargs.publish) topic = wargs.publish;
    
    if(shown) {
        //show the controls since it is hidden
        Dom.setStyle(uuid,'display','block');
    }
    
    
    /**
     * Returns the search results for the query provided
     * if the query is not provided,
     * the default is the value of query textbox.
     *
     * @param query search query (optional)
     * @param num number of results returned - default 10, max 100 (optional)
     */
    this.searchWeb = function(query,num) {
        if((typeof query == 'undefined') || (typeof num == 'undefined')) {
            //take them from controls...
            query = document.getElementById(uuid + "_query").value;
            num = document.getElementById(uuid + "_num").value;
        }
        var encodedQuery = encodeURIComponent(query);
        var encodedNum = encodeURIComponent(num);
        var encodedParam = encodeURIComponent("output=json&query=" + encodedQuery + 
        "&results=" + encodedNum);
        var url = service + "?id=yahoosearch&urlparams=" + encodedParam;
        jmaki.doAjax({
            url: url, 
            callback: function(req) { 
                postProcess(req,ON_SEARCH);
            }
        });
    }
    
    /**
     * Suggest a spelling for the query provided
     * if the query is not provided,
     * the default is the value of query textbox.
     *
     * @param query word or sentence to be checked for spelling (optional)
     */
    this.suggestSpelling = function(query) {
        if(typeof query == 'undefined') {
            //take them from controls...
            query = document.getElementById(uuid + "_query").value;
        }
        var encodedQuery = encodeURIComponent(query);
        var encodedParam = encodeURIComponent("output=json&query=" + encodedQuery);
        var url = service + "?id=yahoospell&urlparams=" + encodedParam;
        jmaki.doAjax({
            url: url, 
            callback: function(req) { 
                postProcess(req,ON_SUGGEST_SPELL);
            }
        });
    }
    
    /**
     * Handler for search results
     * @param resp the JSON object from Y! webservices
     * @param r the document element for showing the results
     */
    function onSearch(resp,r) {
        var v = {success:false};
        if(shown) {
            //if the widget is not hidden, then show the results
            var ul = document.createElement("ul");
            var result = resp.ResultSet.Result;
            for (var i = 0; i < result.length; i++) {
                var li = document.createElement('li');
                var a = document.createElement('a');
                a.href = result[i].Url;
                a.innerHTML = result[i].Title;
                li.appendChild(a);
                ul.appendChild(li);
            }
            r.appendChild(ul);
        }
        if(typeof resp.ResultSet.Result != 'undefined') {
            v = {success:true,results:resp.ResultSet.Result};
        }
        return v;
    }

    /**
     * Handler for spell suggestion results
     * @param resp the JSON object from Y! webservices
     * @param r the document element for showing the results
     */
    function onSpellSuggest(resp,r) {
        var v = {success:false};
        if(shown) {
            if(typeof resp.ResultSet.Result != 'undefined') {
                r.innerHTML = 'suggested spelling: ' + resp.ResultSet.Result;
            } else {
                r.innerHTML = 'No suggested spelling';
            }
        } 
        if(typeof resp.ResultSet != 'undefined') {
            var t = (typeof resp.ResultSet.Result != 'undefined') ?
                resp.ResultSet.Result : "";
            v = {success:true,result:t};
        }
        return v;
    }
    
    /**
     * called by doAjax after finishing an HTTP request
     */
    function postProcess(req,evtName) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var v = {success:false};
                if(req.responseText.length > 0) {
                    var resp = eval("(" + req.responseText + ")");
                    var r = document.getElementById(uuid + '_results');
                    r.innerHTML = '';
                    switch(evtName) {
                        case ON_SEARCH: 
                            v = onSearch(resp,r);
                            break;
                        case ON_SUGGEST_SPELL:
                            v = onSpellSuggest(resp,r);
                            break;
                        default:
                            YAHOO.log("invalid event name","warn");
                    }
                } 
                //the new format is here (as in v) with status flag sent
                jmaki.publish(topic + evtName, {id: uuid, value:v} )
            } 
        }
    }    
    
}