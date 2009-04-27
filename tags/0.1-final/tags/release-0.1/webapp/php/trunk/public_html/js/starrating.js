var base= "images/star_";
var on = new Array();
var off = new Array();
if (document.images) {
	on[0] = new Image;	on[0].src = base + "on.png";
	on[1] = new Image;	on[1].src = base + "on.png";
	on[2] = new Image;	on[2].src = base + "on.png";
	on[3] = new Image;	on[3].src = base + "on.png";
	on[4] = new Image;	on[4].src = base + "on.png";
	off[0] = new Image;	off[0].src = base + "off.png";
	off[1] = new Image;	off[1].src = base + "off.png";
	off[2] = new Image;	off[2].src = base + "off.png";
	off[3] = new Image;	off[3].src = base + "off.png";
	off[4] = new Image;	off[4].src = base + "off.png";
}
function overStars(starIndex,rating) {
	if (document.images) {
		for (i=1;i<=5;i++) {
			if (i <= starIndex) {
				document.images['star_' + i].src = on[i - 1].src;
			} else {
				document.images['star_' + i].src = off[i - 1].src;
			}
		}
	}
}
function outStars(starIndex,rating) {
	if (document.images) {
		for (i=1;i<=5;i++) {
			if (i <= rating) {
				document.images['star_' + i].src = on[i - 1].src;
			} else {
				document.images['star_' + i].src = off[i - 1].src;
			}
		}
	}
}

function editoverStars(starIndex,rating) {
        if (document.images) {
                for (i=1;i<=5;i++) {
                        if (i <= starIndex) {
                                document.images['editstar_' + i].src = on[i - 1].src;
                        } else {
                                document.images['editstar_' + i].src = off[i - 1].src;
                        }
                }
        }
}
function editoutStars(starIndex,rating) {
        if (document.images) {
                for (i=1;i<=5;i++) {
                        if (i <= rating) {
                                document.images['editstar_' + i].src = on[i - 1].src;
                        } else {
                                document.images['editstar_' + i].src = off[i - 1].src;
                        }
                }
        }
}

 var ratinglink = "rateit.php?rating=";
    function handleRating() {
         if (http.readyState == 4) {
             result = http.responseText;
             document.getElementById("ratingText").innerHTML=result;
         }
    }
    function rateEvent(rating) {
         http.open("GET", ratinglink + escape(rating), true);
         http.onreadystatechange = handleRating;
         http.send(null);
    }
    function handleEditRating() {
         if (http.readyState == 4) {
             result = http.responseText.split(",");
             document.getElementById(result[0]).innerHTML=result[1];
         }
    }
    function editrateEvent(rating,cid) {
         http.open("GET", ratinglink + escape(rating) + "&commentid=" + escape(cid) , true);
         http.onreadystatechange = handleEditRating;
         http.send(null);
    }

function ShowHideLayer(divID) {
	var box = document.getElementById(divID);	
		
	if(box.style.display == "none" || box.style.display=="") {
		box.style.display = "block"; 		
	}
	else {
		box.style.display = "none";		
	}
}











