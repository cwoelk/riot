<#assign ServletUtils = statics["org.riotfamily.common.web.support.ServletUtils"] />

<@template.set bodyClass="fullwidth screen" />

<@template.set stylesheets=[
	"style/list.css",
	"style/list-custom.css",
	"style/form.css",
	"style/form-custom.css",
	"style/command.css",
	"style/command-custom.css"
] />

<@template.extend file="../screen.ftl">

	<@template.block name="main">
		<#if chooser??>
			<@template.set bodyClass="chooser" />
		</#if>
		<@riot.script src="/engine.js" />
		<@riot.script src="/util.js" />
		<@riot.script src="/interface/ListService.js" />
		<@riot.script src="riot/pager.js" />
		<@riot.script src="list.js" />
		<@riot.script src="multi-list.js" />
		<div id="list" class="controlList"></div>
		<iframe id="mainList" src="${ServletUtils.addParameter(c.url(mainListLink.url), "embedded", "true")}" marginheight="0" marginwidth="0" frameborder="0" scrolling="no"></iframe>

		<script type="text/javascript" language="JavaScript">
			var list = new RiotList('${controlListState.key}');
			list.render('list');

			var multiList = new RiotMultiList(list);
			multiList.wire('mainList');
		</script>
	</@template.block>

	<@template.block name="extra">
	</@template.block>

</@template.extend>
