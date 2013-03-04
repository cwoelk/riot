var RiotMultiList = Class.create({

	initialize: function(controlList) {
		this.controlList = controlList;
	},

	wire: function(target) {
		this.mainListFrame = $(target);
		this.mainListFrame.update = this.updateControlList.bind(this);
		this.mainListFrame.resize = this.resizeMainListFrame.bind(this);
		this.controlList.table.observe('list:selectionChanged',
			this.handleControlSelectionChanged.bind(this));
	},

	getMainList: function() {
		return this.mainListFrame.contentWindow.list;
	},

	resizeMainListFrame: function(dimensions) {
		this.mainListFrame.setStyle({ height: dimensions.height + 'px' });
	},

	updateControlList: function(parentId, state) {
		console.log(parentId, state);
		var row = ListRow.get(parentId);
		if (row) {
			row.expand.call(row);
		}
		console.log(row);
	},

	handleControlSelectionChanged: function(ev) {
		console.log(ev);
		if (ev.memo.length > 0) {
			ev.memo.each(function (item) {
				console.log(item);
			});
			var mainList = this.getMainList();
			ListService.gotoItemScreenUrl(this.controlList.key, ev.memo, true,
				mainList.processCommandResult.bind(mainList));
		}
	}

});
