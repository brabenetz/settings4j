/*global $:false */
var creatlistItem = function(version) {
	"use strict";
	return "<li><a href=\"" + version + "/index.html\">" + version + "</a></li>";
			
};
$(document).ready(function() {
	"use strict";
	
	$.getJSON( "https://api.github.com/repos/brabenetz/settings4j/contents/archiv?ref=gh-pages", function( data ) {
		$("#versionLinks").append(creatlistItem("current"));
		$.each( data, function( index, fileOrFolder ) {
			if (fileOrFolder.type === 'dir') {
				$("#versionLinks").append(creatlistItem(fileOrFolder.name));
			}
	      });
		});
	
});
