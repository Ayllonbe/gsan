

function Table() {
    //sets attributes
    this.header = [];
    this.data = [[]];
    this.tableClass = '';
    this.tableID = '';
}

Table.prototype.setHeader = function(keys) {
    //sets header data
    this.header = keys
    return this
}

Table.prototype.setData = function(data) {
    //sets the main data
    this.data = data
    return this
}

Table.prototype.setTableClass = function(tableClass) {
    //sets the table class name
    this.tableClass = tableClass
    return this
}
Table.prototype.setTableID = function(tableID) {
    //sets the table class name
    this.tableID = tableID;
    return this
}

Table.prototype.build = function(container) {

    //default selector
    container = container || '#tableResult'

    //creates table
    var table = $('<table id="' +this.tableID +'" class="'+this.tableClass+'"></table>').addClass(this.tableClass)

    var tr = $('<tr></tr>') //creates row
    var th = $('<th></th>') //creates table header cells
    var td = $('<td></td>') //creates table cells

    var header = tr.clone() //creates header row

    //fills header row
    this.header.forEach(function(d) {
    	var thClone = th.clone();
    	thClone.text(d);
    	
        header.append(thClone);
       
    })

    //attaches header row
    table.append($('<thead></thead>').append(header))

    //creates
    var tbody = $('<tbody></tbody>')

    //fills out the table body
    this.data.forEach(function(d) {
        var row = tr.clone() //creates a row
        d.forEach(function(e,j) {
            row.append(td.clone().text(e)) //fills in the row
        })
        tbody.append(row) //puts row on the tbody
    })

    $(container).append(table.append(tbody)) //puts entire table in the container

    return this
}




