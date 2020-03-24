			// ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
			// 
			// Coded by Travis Beckham
			// http://www.squidfingers.com | http://www.podlob.com
			// If want to use this code, feel free to do so, but please leave this message intact.
			//
			// ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

			tooltip = {
				name : "tooltipDiv",
				offsetX : 40,
				offsetY : -10,
				tip : null
			};

			tooltip.init = function () {
				if (!document.getElementById) return;
	
				// It would be nice to be able to generate the tooltip div, 
				// but when using document.createElement Explorer5/MacOS9, 
				// the tooltip div becomes 100% of the window height.
				// Therefore, we have to use document.getElementById to access
				// a div that is already in the body.
	
				// this.tip = document.createElement ("div");
				// this.tip.setAttribute ("id", this.name);
				// document.body.appendChild (this.tip);
	
				this.tip = document.getElementById (this.name);
				if (this.tip) document.onmousemove = function (evt) {tooltip.move (evt)};
	
				var a;
				var anchors = document.getElementsByTagName ("a");
				for (var i = 0; i < anchors.length; i ++) {
					a = anchors[i];
					if (a.className == "tooltip") {
						a.onmouseover = function () {tooltip.show (this.title)};
						a.onmouseout = function () {tooltip.hide ()};
					}
				}
			};

			tooltip.move = function (evt) {
				var x=0, y=0;
				if (document.all) {// Explorer
	
				// Explorer5 contains the documentElement object but it's empty, 
				// so we must check if the scrollLeft property is available.
		
				// If Explorer6 is in Quirks mode, the documentElement properties 
				// will still be defined, but they will contain the number 0.
		
				// If Explorer6 is in Standards compliant mode, the document.body 
				// properties will still be defined, but they will contain the number 0.
		
					x = (document.documentElement && document.documentElement.scrollLeft) ? document.documentElement.scrollLeft : document.body.scrollLeft;
					y = (document.documentElement && document.documentElement.scrollTop) ? document.documentElement.scrollTop : document.body.scrollTop;
					x += window.event.clientX;
					y += window.event.clientY;
		
				} else {// Mozilla
					x = evt.pageX;
					y = evt.pageY;
				}
				// If the style property value is not a string containing the unit measurement,
				// browsers in standard compliant mode will not set the property.
				this.tip.style.left = (x + this.offsetX) + "px";
				this.tip.style.top = (y + this.offsetY) + "px";
			};

			tooltip.show = function (text) {
				if (!this.tip) return;
				this.tip.innerHTML = text;
				// Without the next line, Explorer5/Mac has a redraw problem.
				this.tip.style.visibility = "visible";
				this.tip.style.display = "block";
			};

			tooltip.hide = function () {
				if (!this.tip) return;
				// Without the next line, Explorer5/Mac has a redraw problem.
				this.tip.style.visibility = "hidden";
				this.tip.style.display = "none";
				this.tip.innerHTML = "";
			};

			window.onload = function () {
				tooltip.init ();
			}
