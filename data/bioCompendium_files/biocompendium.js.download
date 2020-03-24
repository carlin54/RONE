/*
#################################################################
##                                                             ##       
## Developed by Venkata P. Satagopam as part of his PhD thesis ##
##                       July 2009                             ##
##                 venkata.satagopam@embl.de                   ##
##                                                             ##
## Disclaimer: Copy rights reserved by EMBL, please contact    ## 
##             Venkata Satagopam  prior to reuse any part      ##
##             of this code                                    ##
##                                                             ##
#################################################################
*/
	function changeImage(imgName){
                minus = new Image();
                minus.src="/images/minus.gif";

                plus = new Image();
                plus.src ="/images/plus.gif";

                if (imgName == 'expand') {
                        for (var i=1; i<=1200; i++) {
                                document['img' + i].src=minus.src;
                        }
                }
                else if (imgName == 'collapse') {
                        for (var i=1; i<=1200; i++) {
                                document['img' + i].src=plus.src;
                        }
                }
                else {
                        var reg = new RegExp("plus");
                        if (reg.test(document[imgName].src)){
                                document[imgName].src=minus.src;
                        }
                        else{
                                document[imgName].src=plus.src;
                        }
                }

        }


	function getItem(id){
		var itm = false;
		if(document.getElementById)
			itm = document.getElementById(id);
		else if(document.all)
			itm = document.all[id];
		else if(document.layers)
			itm = document.layers[id];
        	return itm;
	}

	function toggleAll(dowhat){
		var tags = document.getElementsByTagName('tbody');
		if(!tags)
			return false;

		for(var i = 0; i < tags.length; i++){
			if(tags[i].className == 'collapse_obj'){
				if(dowhat == 'collapse')
					tags[i].style.display = 'none';
				else
					tags[i].style.display = '';
			}
		}
		return false;
	}

	function toggleItem(id){
		itm = getItem(id);
		if(!itm)
			return false;

		if(itm.style.display == 'none')
			itm.style.display = '';
		else
			itm.style.display = 'none';

		return false;
	}

