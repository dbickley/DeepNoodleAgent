	 function getDuration(millis){
	    var dur = {};
	    var units = [
	        {label:"nanos",    mod:1000000},
	        {label:"millis",    mod:1000},
	        {label:"seconds",   mod:60},
	        {label:"minutes",   mod:60},
	        {label:"hours",     mod:24},
	        {label:"days",      mod:31}
	    ];
	   
	    units.forEach(function(u){
	        millis = (millis - (dur[u.label] = (millis % u.mod))) / u.mod;
	    });
	  
	    var nonZero = function(u){ return dur[u.label]; };
	    dur.toString = function(){
	        return units
	            .reverse()
	            .filter(nonZero)
	            .map(function(u){
	                return dur[u.label] + " " + (dur[u.label]==1?u.label.slice(0,-1):u.label);
	            })
	            .join(', ');
	    };
	    return dur;
	};
	
	var createMap = function(array, property) {
	    var objMap = {};
	    array.forEach(function(obj) {
	      objMap[obj[property]] = obj;
	    });
	    return objMap;
	 };
	 
	 function createTree(list,idField,keyField) {
	    var map = {}, node, roots = [], i;
	    for (i = 0; i < list.length; i += 1) {
	        map[list[i][idField]] = i; // initialize the map
	        list[i].children = []; // initialize the children
	    }
	    for (i = 0; i < list.length; i += 1) {
	        node = list[i];
	        if (node[keyField] != "0") {
	            // if you have dangling branches check that map[node.parentId] exists
	            list[map[node[keyField]]].children.push(node);
	        } else {
	            roots.push(node);
	        }
	    }
	    return roots;
	}

		
	
	function getMinMaxAvg(arr, prop) {
	    var max=0;
	    var min=Number.MAX_SAFE_INTEGER;
	    var sum=0;
	    
	    for (var i=0 ; i<arr.length ; i++) {
	    		var	value=parseInt(arr[i][prop]);
	        	max = Math.max(value,max);
	        	min = Math.min(value,min);
	        	sum += value;
	    }
	    return [min,max,parseInt(sum/arr.length)];
	}
	function scaleBetween(unscaledNum, minAllowed, maxAllowed, min, max) {
		  return (maxAllowed - minAllowed) * (unscaledNum - min) / (max - min) + minAllowed;
	}