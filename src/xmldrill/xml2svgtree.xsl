<?xml version="1.0" encoding="UTF-8"?>
<!--
XML Tree Drawer
This XSLT stylesheet converts an XML tree into an SVG image
of a tree consisting of element nodes.
Copyright (C) 2004 Weston Ruter <http://weston.ruter.net/>

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will core useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
-->
<xsl:stylesheet version="2.0"
  xmlns="http://www.w3.org/2000/svg"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" />
	<xsl:output encoding="UTF-8"
	  indent="yes"
	  method="xml" 
	  version="1.0"
	  media-type="image/svg+xml"/>

	<xsl:template match="/">
<xsl:comment>
SVG Tree Drawer
Copyright (C) 2004 Weston Ruter

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will core useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
</xsl:comment>
		<svg onload="init()">
			<title>Tree</title>
			<style type="text/css">
<![CDATA[
g text {
	text-anchor:middle;
	font-family:'Lucida Console Unicode', Arial, Helvetica, Serif;
	
}

g line {
	stroke:black;
}
]]>
			</style>
			<script type="text/ecmascript">
<![CDATA[
var fontSize = 15;
var wordSpacing = 20; //wordSpacing/2 == text padding
var marginLeft = 2;
var marginTop = 2;
var width = 0;   //read only
var height = 0;  //read only
var branchHeight = fontSize;
var branchPaddingTop = fontSize/4;
var branchPaddingBottom = 0;


window.onerror = function(message, url, line){
	alert("Javascript error: " + message + "\n" + url + "\nOn line: " + line + "\nReport error to: weston@linguiste.org");
	return false;
}

var rootNode;
function init(){
	var gElements = document.getElementsByTagName('g');
	rootNode = gElements.item(0);
	drawTree();
}


//positions all the nodes in a tree structure
var buffer = '';
function drawTree(){ 
	try {
		r_drawTree(rootNode, null, false, marginLeft, marginTop + fontSize, 0);
	}
	catch(error){
		alert(error + "\nReport error to: weston@linguiste.org\n\n" + dump(error));
	}
}
function dump(obj){
	var buffer = '';
	for(key in obj){
		buffer += key + ' = ' + obj[key] + "\n";
	}
	return buffer;
} 
function r_drawTree(containerNode, parentLabel, isEmptyParent, shiftLeft, shiftTop, parentWidth){//returns the width of node's children
	var isEmpty = false;
	var isParent = (containerNode.getElementsByTagName('g').length ? true : false);
	var childrenWidth = 0;
	var childrenLabels = Array();
	var nodeLabel;
	var nodeBranch;
	var nodeExtendBranch;
	var nodeLabelWidth;
	var leafY;
	
	//get and handle children ============================
	for(var i = 0; i < containerNode.childNodes.length; i++){
		if(containerNode.childNodes.item(i).nodeName == 'text'){
			nodeLabel = containerNode.childNodes.item(i);
			
			//determine the width of the label
			if(!nodeLabel.firstChild || !nodeLabel.firstChild.nodeValue || nodeLabel.firstChild.nodeValue.match(/^\s*$/) ){
				nodeLabelWidth = wordSpacing;
				isEmpty = true;
			}
			else {
				nodeLabelWidth = wordSpacing + nodeLabel.getComputedTextLength();
				isEmpty = false;
			}
			if(nodeLabelWidth < parentWidth)
				nodeLabelWidth = parentWidth;
		}
		//connecting branch 
		else if(containerNode.childNodes.item(i).nodeName == "line"){
			nodeBranch = containerNode.childNodes.item(i);
		}
		//children nodes
		else if(containerNode.childNodes.item(i).nodeName == "g"){
			var returned = r_drawTree(containerNode.childNodes.item(i), nodeLabel, isEmpty, shiftLeft + childrenWidth, shiftTop + (isEmpty ? branchHeight : branchHeight + fontSize), nodeLabelWidth);
			childrenLabels.push(returned[0]);
			childrenWidth += returned[1];
		}
	}
	
	//draw label, children, and branches ==================	
	if(!childrenWidth) //there are no children; this is the branch end
		childrenWidth = nodeLabelWidth;
	if(nodeLabel == null)
		throw Error("Error: Every child must have a label (every <g> must contain a <text>, even if it is empty). Revise your XSLT stylesheet.");

	//position label
	var thisY = shiftTop;
	var thisX;
	if(childrenLabels.length){
		var firstChild = parseFloat(childrenLabels[0].label.getAttribute('x'));
		var lastChild = parseFloat(childrenLabels[childrenLabels.length-1].label.getAttribute('x'));
		thisX = firstChild + (lastChild - firstChild)/2;
	}
	else
		thisX = shiftLeft + childrenWidth/2;
	if(thisY > height) 
		height = thisY;
	if(shiftLeft + childrenWidth > width) 
		width = shiftLeft + childrenWidth;
	nodeLabel.setAttribute('y', thisY + 'px');
	nodeLabel.setAttribute('x', thisX + 'px');
	
	//connect branches from child labels to parent label
	leafY = thisY;
	for(var i = 0; i < childrenLabels.length; i++){
		childrenLabels[i].branch.setAttribute('x1', thisX + 'px');
		childrenLabels[i].branch.setAttribute('y1', (thisY + (isEmpty ? -fontSize : branchPaddingTop)) + 'px');
	}

	//below root: anchor one end of the branch to the label
	if(containerNode != rootNode){
		if(nodeBranch){
			nodeBranch.setAttribute('x2', nodeLabel.getAttribute('x'));
			nodeBranch.setAttribute('y2', (parseFloat(nodeLabel.getAttribute('y')) - fontSize - branchPaddingBottom) + 'px');
		}
	}
	//else if(nodeBranch) //hide it if it was accidentally included in the source code
	//	nodeBranch.style.display = 'none';
	return Array({label:nodeLabel, branch:nodeBranch}, childrenWidth);
}
]]>
			</script>
			<xsl:apply-templates />

		</svg>
	</xsl:template>
	

    <xsl:template match="node()">
        <xsl:if test="local-name() or normalize-space(.)">
        <g>
            <xsl:if test=". = /">
                <xsl:attribute name="id">tree</xsl:attribute>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="local-name()"><text>&lt;<xsl:value-of select="local-name()"/>&gt;</text></xsl:when>
                <xsl:otherwise><text fill="blue"><xsl:value-of select="."/></text></xsl:otherwise>
            </xsl:choose>
            <line/>
            <xsl:apply-templates select="node() | @*"/>
        </g>
        </xsl:if>
    </xsl:template>
            <!-- 
            <xsl:for-each select=".">
                <xsl:apply-templates select="./*"/>  
            </xsl:for-each>
             -->

    <xsl:template match="@*">
        <g>
            <text fill="green"><xsl:value-of select="local-name(.)"/>=<xsl:value-of select="."/></text>
            <line/>
            <xsl:apply-templates/>
        </g>
    </xsl:template>

<!-- 
    <xsl:template match="/ | node(  ) | @*">
      <xsl:copy>
        <xsl:apply-templates select="@*"/>
        <xsl:apply-templates/>
      </xsl:copy>
    </xsl:template>

 -->
    
</xsl:stylesheet>