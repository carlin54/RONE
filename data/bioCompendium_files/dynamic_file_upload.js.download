			var upload_number = 2; 
			function addFileInput() { 
				var d = document.createElement("div"); 
				var l = document.createElement("a"); 
				var r = document.createElement("img"); 

				var space = document.createTextNode(' ');

				var org_select = document.createElement("select");
				org_select.setAttribute("id", "Category"+upload_number);
				org_select.setAttribute("name", "Category"+upload_number);
				org_select.setAttribute("onChange", "SelectSubCatAll(this.id);");
//				org_select.setAttribute("onfocus", "alert(this.id);");
//				org_select.setAttribute("onChange", "SelectSubCat(upload_number);");

				var file = document.createElement("input"); 
				file.setAttribute("type", "file"); 
				file.setAttribute("name", "attachment"+upload_number); 

				var text = document.createElement("input"); 
				text.setAttribute("type", "text"); 
				text.setAttribute("size", "8"); 
				text.setAttribute("id", "gene_list_"+upload_number); 
				text.setAttribute("name", "gene_list_"+upload_number); 
				text.setAttribute("value", "gene_list_"+upload_number); 

				var select = document.createElement("select");
				select.setAttribute("id", "SubCat"+upload_number);
				select.setAttribute("name", "SubCat"+upload_number);

				l.setAttribute("href", "javascript:removeFileInput('f"+upload_number+"');"); 
//				l.appendChild(document.createTextNode("Remove")); 
				r.setAttribute("src", "images/custom_small_delete.gif");	
				l.appendChild(r); 
				d.setAttribute("id", "f"+upload_number); 
				d.appendChild(org_select); 
//				d.appendChild(space); 
				d.appendChild(text); 
//				d.appendChild(space); 
				d.appendChild(file); 
//				d.appendChild(space); 
				d.appendChild(select); 
				d.appendChild(space); 
				d.appendChild(l); 
				document.getElementById("moreUploads").appendChild(d); 
				fillCategory(upload_number);
//				SelectSubCat(upload_number);
				upload_number++; 
			}

        		function removeFileInput(i) { 
				var elm = document.getElementById(i); 
				document.getElementById("moreUploads").removeChild(elm); 
			}
