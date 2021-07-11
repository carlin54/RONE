<h1  style="text-align:center" width=0%; style="font-size:40px;">RONE<img src="https://raw.githubusercontent.com/carlin54/RONE/master/icons/rone_icon.png" align="Right"></h1>
<h4>What is RONE?</h4>
RONE is a tool designed for <a href="http://www.garuda-alliance.org/">Garuda Platform</a> to organise data from several sources. This application was implemented in Java, using JSwing, GarduaSDK, IntermineFramework, Derby, and PF4J. <br>This application supports run-time plugins for searching databases. 
<br>
<h4>How to install RONE?</h4>
<ol>
<li>Download the latest package <a href="https://raw.githubusercontent.com/carlin54/RONE/master/package/11136dd6-baa0-49c0-9c1e-d2bec673eec6.zip">here</a> (1.2.2).<br></li> 
<li>Open the Garuda Platform, load Gadget Installer <img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/30.png" border="1px solid red" width = "32px" height="32px"/>.</li>
<li>Load the package to the using the Gadget Installer <img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/31.png" border="1px solid red" width = "32px" height="32px"/>.</li>
</ol>
<i>Make sure you have the latest Java Runtime Enviroment.</i>
<h4>Licence</h4>
This project is covered under the  <a href="https://raw.githubusercontent.com/carlin54/RONE/master/LICENCE">GNU LGPL v3 Licence</a>.
<h4>Changelog</h4>
<ul>
<li>Generated documentation</li>
<li>Fixed the join table issues</li>
<li>Added UUID use for setting import settings</li>
</ul>
Tuesday, 13. April 2021 12:59PM 
<h2>Tables</h2>
<details>
<summary>
How to import data from a file? 
</summary>
<div style="margin-left: 5%">
<br>
RONE accepts two types of file formats, comma-separated value and tab-delimited text.<br>
<ol>
<li>
To import import a file, select <b>File</b> > <b>Import</b> > <b> from File</b> <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/1.png" border="1px solid red"/>
<br>
<br>
</li>
<li>
Navigate to the file that you are interested in importing. <b>Select</b> the file and then click <b>Open</b>. <br> 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/2.png" border="1px solid red"/><br>
</li>
<li>
<b> Select</b> the format for the file that you are trying to load. <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/3.png" border="1px solid red"/>
<br>
<br>
</li>
<li>
Once the file format has been selected, press <b> OK</b>. <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/4.png" border="1px solid red"/>
<br>
<br>
</li>
<li>
Your data will be loaded from the file and presented to you in a new tab.  <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/5.png" border="1px solid red"/><br>
<br>
</li>
</ol>
</div>
</details>
<details>
<summary>
How to import data from a Garuda Gadget?
</summary>
<div style="margin-left: 5%">
<br>
Formats that RONE accepts as inputs 
<table style="width:100%">
<tr>
<th>File Type</th>
<th>File Format</th>
</tr>
<tr>
<td>Genelist</td>
<td>TXT</td>
</tr>
<tr>
<td>Genelist</td>
<td>CSV</td>
</tr>
<tr>
<td>Ensemble</td>
<td>TXT</td>
</tr>
<tr>
<td>Ensemble</td>
<td>CSV</td>
</tr>
</table>
<br>
<ol>
<li>To import data into RONE from a Garuda Gadget.<br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/32.png" border="1px solid red"/>
</li>
<ol>
<li>It will try import the data based on configurable import settings.<br><br>
</li>
<li>If they havn't been set, it will try to import the data automatically based on the extension of the file given.<br><br>
</li>
</ol>
<li>You will be able the imported data in the new table.<br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/33.png" border="1px solid red"/>
</li>
</ol>
</div>
</details>
<details>
<summary>
How to add/remove/edit import settings for other Garuda Gadgets?
</summary>
<div style="margin-left: 5%">

<ol>
<li>
In your file explorer, navigate to the folder where Garuda Platform is located.<br>
<br>
<br>
</li>

<li>
Locate and open the folder/file "11136dd6-baa0-49c0-9c1e-d2bec673eec6/config.txt".<br><br>
You should see the following:<br>

    Garuda.GeneMapper.seperator=,
    Garuda.Reactome\ gadget.seperator=,
    Garuda.GeneMapper.column_headers=Gene, NM, TF, Region, Strand, MA Score, PSSM Score, ID, Motif, Similarity, Pareto
    Garuda.Reactome\ gadget.column_headers=Name, Species, Disease Association, Inferred Association, Pathways
    Garuda.Reactome\ gadget.skip_header=true
    Garuda.GeneMapper.skip_header=true
    
<br>
<br>
</li>
<li>
To add an import setting for a gadget, you will need to add three lines into the configuration file. You can use its UUID (preferable), or you can use the gadget's name to reference the settings for your gadget. In the above case, the gadgets name was used "Reactome gadget". In the following case it will be "MyExampleGadget". 
<ol>
<li>
Firstly, the "column_headers" refer to the column names of the data provided by the gadget. <br>

    Garuda.MyExampleGadget.column_headers=Example Header 1, Example Header 2, Example Header...

<br>
<br>
</li>
<li>
Secondly, the "seperator" refer the character used to seperate cells. For a CSV-file, that would be ",".<br>

    Garuda.MyExampleGadget.seperator=,
    
<br>
<br>
</li>
<li>
Finally, the "skip_header" refer tell RONE weather or not the first row of data being imported should be discarded. This is useful if the column headers are contained in the first-row of the incoming data. This value can <b>ONLY</b> be either "true" or "false". <br>

    Garuda.MyExampleGadget.skip_header=true
    
<br>
<br>
</li>
</ol>
</li>
</div>
</details>
<details>
<summary>
How to export data to a file?<br>
</summary>
<div style="margin-left: 5%">
<br>
RONE only supports exporting data to a comma-separated value file.<br>
<ol>
<li>
To export data from a table to a file. <b> Select</b> the cells that you would like to export. If no cells are selected, the <i>whole table</i> will be used at the selection.  <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/6.png" border="1px solid red"/>
<br>
<br>
</li>
<li>
Select <b> File</b> > <b> Export</b> > <b> to File</b> <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/7.png" border="1px solid red"/>
<br>
<br>
</li>
<li>
<ol>
<li>
Navigate to the directory where you would like to store the table/selection.
</li>
<li>
Insert the name for the new file that will be generated. 
</li>
<li>
Click <b> Save</b>. 
</li>
</ol>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/12.png" border="1px solid red"/>
<br>
<br>
</li>
</ol>
</div>
</details>
<details>
<summary>
How to export data to a Garuda Gadget?
</summary>
<div style="margin-left: 5%">
<br>
<ol>
<li>
To export data from a table to a gadget. <b> Select</b> the cells that you would like to export. If no cells are selected, the <i>whole table</i> will be as the selection. <br> 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/6.png" border="1px solid red"/>
<br>
<br>
</li>
<li>
Navigate to <b>File</b> > <b>Export</b> > <b>to Garuda</b> > <b>(export Setting)</b>. Select the export option with the desired file type, and file format.<br> 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/35.png" border="1px solid red"/>
<br>
<br>
</li>
<li>
To export data from a table to a gadget. <b> Select</b> the cells that you would like to export. If no cells are selected, the <i>whole table</i> will be as the selection. <br> 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/34.png" border="1px solid red"/>
<br>
<br>
</li>
</ol>
</div>
</details>
<details>
<summary>
How to export data to a new table?<br>
</summary>
<div style="margin-left: 5%">
<br>
<ol>
<li>
To export data from a table to a file. <b> Select</b> the cells that you would like to export. If no cells are selected, the <i>whole table</i> will be as the selection. 					<br> 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/6.png" border="1px solid red"/>
<br>
<br>
</li>
<li>
To import import a file, select <b> File</b> > <b> Export</b> > <b> to File</b> <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/15.png" border="1px solid red"/>
<br>
<br>
</li>
<li>
<ol>
<li>
Enter the name for the new table.<br>
</li>
<li>
Press <b> OK</b>. <br>
</li>
</ol>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/13.png" border="1px solid red"/>
<br>
<br>
</li>
<li> 
The selected data will be presented in the new tab. <br> 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/14.png" border="1px solid red"/>
<br>
<br>
</li>
</ol>
</div>
</details>

<h2>Table Operations</h2>
<details>
<summary>
How to sort a table?<br>
</summary>
<div style="margin-left: 5%">
<br>
Tables can be sorted by their columns in ascending and descending order. Further, order by operations can be employed as well; sorting by column <i>A</i>, then by column <i>B</i>. 
<br>
<ol>
<li>
<b> Right-click</b> the column header for the column that you would like to sort. <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/8.png" border="1px solid red"/>
<br>
<br>
</li>
<ol>
<li>
<b>Left-click</b> the name of the <b> Sort by <i>column header</i></b> from the pop-up menu that appears. By default, the column will sort in ascending order. <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/9.png" border="1px solid red"/>
<br>
<br>
</li>
<li>
If you would like to sort the column in descending order. Then <b>Right-click</b> the column header from step 1. Next, <b>Left-click</b> the name of the column header from the pop-up menu that appears. You will notice next to the column header, either (↑) or (↓) denotes if the column is currently sorted in ascending or descending order.  <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/10.png" border="1px solid red"/>
<br>
<br>
</li>
</ol>
<li>
After sorting by column <i>A</i>, you can sort column <i>B</i>, then <i>C</i>, <i>D</i>, and so on. This is accomplished by <b> Right-clicking</b> the column you next want to sort by. Then <b> Left-click</b> the name of the <b> Order by <i>column header</i></b> from the pop-up menu that appears. You will notice next to the column header, either (↑) or (↓) denotes if the column is currently being sorted in ascending or descending order.  <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/11.png" border="1px solid red"/>
<br>
<br>
</li>
<li>
If you no longer wish to have the rows ordered with the current sort by, order by filter, you can clear it. This is accomplished by <b> Right-clicking</b> a column header and selecting <b> Clear</b> from the pop-up menu. <br> 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/27.png" border="1px solid red"/>
<br>
<br>
</li>
</ol>
</div>
</details>
<details>
<summary>
How to join tables together?<br>
</summary>
<div style="margin-left: 5%">
<br>
RONE support the standard <a href="https://www.w3schools.com/sql/sql_join.asp">join operations</a> offered by SQL.<br>
<ul>
<li>Left Inclusive</li>
<li>Left Exclusive</li>
<li>Right Inclusive</li>
<li>Right Exclusive</li>
<li>Full Outer Inclusion</li>
<li>Full Outer Exclusion</li>
<li>Inner</li>
</ul>
<small>
<details>
<summary>
Venn Diagrams
</summary>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/26.png" border="1px solid red"/><br>
<br>
</details>
</small>
<small>
<details>
<summary>
<i>Note, RONE only supports "=" constraints.</i>
</summary>

```SQL
SELECT column name(s)
FROM table1
LEFT JOIN table2
ON table1.column_name = table2.column_name;
```

</details>
</small>
<br>
To be able to join two tables together, you will need at least two tables. The tables will ideally contain two columns of the same values. For instance, bellow you see in the <i>SHOE.csv</i> table, there is a column called <i>Gene</i>, and in the table <i>GeneSymbols.txt.csv</i>, there is a column called <i>Gene</i>. The joint will be performed on the same column.<br> 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/16.png" border="1px solid red"/>
<br>
<br>		
<ol>
<li> 
To join two tables together, select <b>Table</b> > <b> Join Table</b>.<br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/17.png" border="1px solid red"/>
<br>
<br>
</li>
<li> 
A new window called Join Table will appear. Using the combo-boxes <i>Table A</i>, and <i>Table B</i> in <i>Table Select section</i>, select the tables you would like to join.<br> 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/18.png" border="1px solid red"/>
<br>
<br>
</li>
<li> 
Now, you will see that it added the columns of the selected tables into the list on the left in <i>Table A section</i>, and <i>Table B section</i>. The list boxes on the right of <i>Table A section</i>, and <i>Table B section</i> denote columns included to the resulting tab. Each section is an exclude and include list (left and right). You can move columns between the include and exclude columns using the buttons:  <b>>></b>, <b><<</b>,  <b>Add All</b>,  <b>Remove All</b>.<br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/19.png" border="1px solid red"/>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/20.png" border="1px solid red"/>
<br>
<br>
</li>
<li> 
From the two combo-boxes at the top of the <i>Join Operation section</i>, select the columns from the tables you would like to constrain. Then, from the combo-box in the bottom-right corner of the <i> Join Operation</i> section, select the type of joint you would like to perform.<br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/22.png" border="1px solid red"/>
<br>
<br>
</li>
<li> 
<ol>
<li> 
Click <b>Add Constraint (=)</b>. You will see the constraint appear in the table. You can add several constraints by simply changing the selected columns, and then clicking <b>Add Constraint (=)</b> again.<br> 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/23.png" border="1px solid red"/>
<br>
<br>
</li>
<li> 
To remove constraints, select the constraint from the table in the Join Operations section. Click <b>Remove Selected</b><br> 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/29.png" border="1px solid red"/>
<br>
<br>
</li>
</ol>
</li>
<li> 
Finally, in the Name Table section, enter the name for the new table. Click <b>Join Table</b>.
<small>
<details>
<summary>
<i>Equivelent to</i>
</summary>

```SQL
SELECT GeneSymbols.Gene, GeneSymbols.Cluster, SHOE.Gene, SHOE.Strand, SHOE.Similarity
FROM GeneSymbols
LEFT JOIN SHOE
ON GeneSymbols.Gene = SHOE.Gene;
```

</details>
</small>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/24.png" border="1px solid red"/>
</li>
<li> 
The resulting table can be seen the new tab. 
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/25.png" border="1px solid red"/> 
</li>
</ol>
</div>
</details>
</details>

<h2>Plugins</h2>
<details>
<summary>
How to use plugins?<br>
</summary>
<div style="margin-left: 5%">
When RONE begins, it trys to activate its plugins. If the plugin has been loaded successfully, it can be accessed from the plugin menu. <br>
<img src="https://raw.githubusercontent.com/carlin54/RONE/master/doc/images/28.png" border="1px solid red"/>
<br>
<br>
<div style="margin-left: 5%">
<details>
<summary>
Default plugins 
</summary>
<div style="margin-left: 5%">
<details>
<summary>
TargetMine 
</summary>
<table style="width:100%">
<tr>
<th>Input</th>
<th>Output</th>
</tr>
<tr>
<td>Gene Symbol</td>
<td>Gene Symbol, Primary Identifer, Name, Pathway Identifer, Pathway Name, Organism Name, Gene Pathway Label 1, Gene Pathway Label 2</td>
</tr>
</table>
<br>
<br>
</details>
</div>
<div style="margin-left: 5%">
<details>
<summary>
Reactome
</summary>
<table style="width:100%">
<tr>
<th>Input</th>
<th>Output</th>
</tr>
<tr>
<td>Gene Symbols</td>
<td>Gene Symbol, Species Name, Pathway Name, Pathway stId, Pathway Disease Association, Pathway III, Entities Ratio, Entities 	pValue, Entities FDR</td>
</tr>
</table>
<br>
<br>
</details>
<details>
<summary>
Percellome
</summary>
<table style="width:100%">
<tr>
<th>Input</th>
<th>Output</th>
</tr>
<tr>
<td>Gene Symbol, Species</td>
<td>Gene Symbol, Probe (Affy ID), Found Gene Symbol</td>
</tr>
<tr>
<td>Probe IDs, Species</td>
<td>Probe ID (Affy ID), Gene Symbol, Biological Function, Cellular Function, Molecular Function</td>
</tr>
</table>
<br>
<br>
</details>
</div>
</details>
</div>
</details>
<details>
<summary>
<strike>How to create plugins?</strike><br>
</summary>
</details>
<details>
<summary>
<strike>How to install plugins?</strike><br>
</summary>
</details>
<br>
