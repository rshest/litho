"use strict";(self.webpackChunk=self.webpackChunk||[]).push([[813,6972,2304,4882,6127,7940,1646,4980],{3905:(e,t,n)=>{n.r(t),n.d(t,{MDXContext:()=>d,MDXProvider:()=>c,mdx:()=>g,useMDXComponents:()=>s,withMDXComponents:()=>m});var a=n(67294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(){return o=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var a in n)Object.prototype.hasOwnProperty.call(n,a)&&(e[a]=n[a])}return e},o.apply(this,arguments)}function l(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?l(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):l(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function p(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},o=Object.keys(e);for(a=0;a<o.length;a++)n=o[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(a=0;a<o.length;a++)n=o[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var d=a.createContext({}),m=function(e){return function(t){var n=s(t.components);return a.createElement(e,o({},t,{components:n}))}},s=function(e){var t=a.useContext(d),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},c=function(e){var t=s(e.components);return a.createElement(d.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},f=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,o=e.originalType,l=e.parentName,d=p(e,["components","mdxType","originalType","parentName"]),m=s(n),c=r,f=m["".concat(l,".").concat(c)]||m[c]||u[c]||o;return n?a.createElement(f,i(i({ref:t},d),{},{components:n})):a.createElement(f,i({ref:t},d))}));function g(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var o=n.length,l=new Array(o);l[0]=f;var i={};for(var p in t)hasOwnProperty.call(t,p)&&(i[p]=t[p]);i.originalType=e,i.mdxType="string"==typeof e?e:r,l[1]=i;for(var d=2;d<o;d++)l[d]=n[d];return a.createElement.apply(null,l)}return a.createElement.apply(null,n)}f.displayName="MDXCreateElement"},85162:(e,t,n)=>{n.r(t),n.d(t,{default:()=>l});var a=n(67294),r=n(34334);const o="tabItem_Ymn6";function l(e){var t=e.children,n=e.hidden,l=e.className;return a.createElement("div",{role:"tabpanel",className:(0,r.Z)(o,l),hidden:n},t)}},65488:(e,t,n)=>{n.r(t),n.d(t,{default:()=>u});var a=n(83117),r=n(67294),o=n(34334),l=n(72389),i=n(67392),p=n(7094),d=n(12466);const m="tabList__CuJ",s="tabItem_LNqP";function c(e){var t,n,l=e.lazy,c=e.block,u=e.defaultValue,f=e.values,g=e.groupId,y=e.className,x=r.Children.map(e.children,(function(e){if((0,r.isValidElement)(e)&&"value"in e.props)return e;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})),h=null!=f?f:x.map((function(e){var t=e.props;return{value:t.value,label:t.label,attributes:t.attributes}})),v=(0,i.l)(h,(function(e,t){return e.value===t.value}));if(v.length>0)throw new Error('Docusaurus error: Duplicate values "'+v.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.');var b=null===u?u:null!=(t=null!=u?u:null==(n=x.find((function(e){return e.props.default})))?void 0:n.props.value)?t:x[0].props.value;if(null!==b&&!h.some((function(e){return e.value===b})))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+b+'" but none of its children has the corresponding value. Available values are: '+h.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");var N=(0,p.U)(),C=N.tabGroupChoices,k=N.setTabGroupChoices,w=(0,r.useState)(b),T=w[0],S=w[1],P=[],E=(0,d.o5)().blockElementScrollPositionUntilNextRender;if(null!=g){var O=C[g];null!=O&&O!==T&&h.some((function(e){return e.value===O}))&&S(O)}var R=function(e){var t=e.currentTarget,n=P.indexOf(t),a=h[n].value;a!==T&&(E(t),S(a),null!=g&&k(g,String(a)))},j=function(e){var t,n=null;switch(e.key){case"ArrowRight":var a,r=P.indexOf(e.currentTarget)+1;n=null!=(a=P[r])?a:P[0];break;case"ArrowLeft":var o,l=P.indexOf(e.currentTarget)-1;n=null!=(o=P[l])?o:P[P.length-1]}null==(t=n)||t.focus()};return r.createElement("div",{className:(0,o.Z)("tabs-container",m)},r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,o.Z)("tabs",{"tabs--block":c},y)},h.map((function(e){var t=e.value,n=e.label,l=e.attributes;return r.createElement("li",(0,a.Z)({role:"tab",tabIndex:T===t?0:-1,"aria-selected":T===t,key:t,ref:function(e){return P.push(e)},onKeyDown:j,onFocus:R,onClick:R},l,{className:(0,o.Z)("tabs__item",s,null==l?void 0:l.className,{"tabs__item--active":T===t})}),null!=n?n:t)}))),l?(0,r.cloneElement)(x.filter((function(e){return e.props.value===T}))[0],{className:"margin-top--md"}):r.createElement("div",{className:"margin-top--md"},x.map((function(e,t){return(0,r.cloneElement)(e,{key:t,hidden:e.props.value!==T})}))))}function u(e){var t=(0,l.default)();return r.createElement(c,(0,a.Z)({key:String(t)},e))}},7772:(e,t,n)=>{n.d(t,{Z:()=>u});var a=n(83117),r=n(67294),o=n(23746),l=n(7694),i=n(13618),p="0.44.0",d="0.45.0-SNAPSHOT",m="0.10.4",s="0.142.0",c=n(86668);const u=function(e){var t=e.language,n=e.code.replace(/{{site.lithoVersion}}/g,p).replace(/{{site.soloaderVersion}}/g,m).replace(/{{site.lithoSnapshotVersion}}/g,d).replace(/{{site.flipperVersion}}/g,s).trim(),u=(0,c.L)().isDarkTheme?i.Z:l.Z;return r.createElement(o.ZP,(0,a.Z)({},o.lG,{code:n,language:t,theme:u}),(function(e){var t=e.className,n=e.style,a=e.tokens,o=e.getLineProps,l=e.getTokenProps;return r.createElement("pre",{className:t,style:n},a.map((function(e,t){return r.createElement("div",o({line:e,key:t}),e.map((function(e,t){return r.createElement("span",l({token:e,key:t}))})))})))}))}},74817:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>u,contentTitle:()=>s,default:()=>y,frontMatter:()=>m,metadata:()=>c,toc:()=>f});var a=n(83117),r=n(80102),o=(n(67294),n(3905)),l=n(65488),i=n(85162),p=n(44996),d=(n(7772),["components"]),m={id:"kotlin-flexbox-containers",title:"Flexbox Containers"},s=void 0,c={unversionedId:"kotlin/kotlin-flexbox-containers",id:"kotlin/kotlin-flexbox-containers",title:"Flexbox Containers",description:"This page covers how to convert existing code from the Java Spec API to the Kotlin API.",source:"@site/../docs/kotlin/flexbox-containers.mdx",sourceDirName:"kotlin",slug:"/kotlin/kotlin-flexbox-containers",permalink:"/docs/kotlin/kotlin-flexbox-containers",draft:!1,editUrl:"https://github.com/facebook/litho/edit/master/website/../docs/kotlin/flexbox-containers.mdx",tags:[],version:"current",frontMatter:{id:"kotlin-flexbox-containers",title:"Flexbox Containers"},sidebar:"mainSidebar",previous:{title:"Migrating from MountSpecs",permalink:"/docs/kotlin/migrating-from-mountspecs"},next:{title:"Event Handling",permalink:"/docs/kotlin/event-handling"}},u={},f=[{value:"Rows and Columns, Revisited",id:"rows-and-columns-revisited",level:2},{value:"Differences between the KComponent API and the Spec API",id:"differences-between-the-kcomponent-api-and-the-spec-api",level:3},{value:"Migrating from the Spec API",id:"migrating-from-the-spec-api",level:2},{value:"Flexbox Properties Cheatsheet",id:"flexbox-properties-cheatsheet",level:3},{value:"Example Migration",id:"example-migration",level:3}],g={toc:f};function y(e){var t=e.components,n=(0,r.Z)(e,d);return(0,o.mdx)("wrapper",(0,a.Z)({},g,n,{components:t,mdxType:"MDXLayout"}),(0,o.mdx)("admonition",{type:"note"},(0,o.mdx)("p",{parentName:"admonition"},"This page covers how to convert existing code from the Java Spec API to the Kotlin API."),(0,o.mdx)("p",{parentName:"admonition"},"For the main Layout spec information, refer to the ",(0,o.mdx)("a",{parentName:"p",href:"/docs/mainconcepts/flexbox-yoga"},"Layout System with Flexbox")," page.")),(0,o.mdx)("h2",{id:"rows-and-columns-revisited"},"Rows and Columns, Revisited"),(0,o.mdx)("p",null,"As with the Spec API, ",(0,o.mdx)("inlineCode",{parentName:"p"},"Row")," and ",(0,o.mdx)("inlineCode",{parentName:"p"},"Column")," are the primary layout containers used in the KComponent API."),(0,o.mdx)("img",{src:(0,p.default)("/images/litho-layout-row-column-simple.png")}),(0,o.mdx)("h3",{id:"differences-between-the-kcomponent-api-and-the-spec-api"},"Differences between the KComponent API and the Spec API"),(0,o.mdx)("p",null,"When using ",(0,o.mdx)("inlineCode",{parentName:"p"},"Row")," and ",(0,o.mdx)("inlineCode",{parentName:"p"},"Column"),", there are a few important differences between the Spec API and KComponent API."),(0,o.mdx)("ol",null,(0,o.mdx)("li",{parentName:"ol"},(0,o.mdx)("strong",{parentName:"li"},"Props of Rows and Columns")," - props that configure the ",(0,o.mdx)("inlineCode",{parentName:"li"},"Row"),"/",(0,o.mdx)("inlineCode",{parentName:"li"},"Column")," itself, like ",(0,o.mdx)("inlineCode",{parentName:"li"},"alignItems")," or ",(0,o.mdx)("inlineCode",{parentName:"li"},"justifyContent"),", now appear as props directly on the container Component. In this way, there is no difference from setting props on any other component."),(0,o.mdx)("li",{parentName:"ol"},(0,o.mdx)("strong",{parentName:"li"},"Common Props")," - props that configure a particular child's positioning within the context of a ",(0,o.mdx)("inlineCode",{parentName:"li"},"Row"),"/",(0,o.mdx)("inlineCode",{parentName:"li"},"Column"),", like ",(0,o.mdx)("inlineCode",{parentName:"li"},"alignSelf")," or ",(0,o.mdx)("inlineCode",{parentName:"li"},"flex"),", now appear with other ",(0,o.mdx)("a",{parentName:"li",href:"/docs/kotlin/kotlin-api-basics#common-props"},"common props")," on the ",(0,o.mdx)("inlineCode",{parentName:"li"},"Style")," passed to that child. This is akin to ",(0,o.mdx)("inlineCode",{parentName:"li"},"LayoutParams")," in vanilla Android in that they must be able to go on any component and are read by the parent."),(0,o.mdx)("li",{parentName:"ol"},(0,o.mdx)("strong",{parentName:"li"},"Children")," - children are added within a trailing lambda using the ",(0,o.mdx)("inlineCode",{parentName:"li"},"child()")," call.")),(0,o.mdx)("p",null,"The following snippet illustrates the points above:"),(0,o.mdx)("pre",null,(0,o.mdx)("code",{parentName:"pre",className:"language-kotlin"},' Column(style = Style.padding(all = 8.dp), alignItems = YogaAlign.CENTER) {\n   child(Text(style = Style.flex(grow = 1f), text = "Foo"))\n   child(Text(text = "Bar"))\n }\n')),(0,o.mdx)("h2",{id:"migrating-from-the-spec-api"},"Migrating from the Spec API"),(0,o.mdx)("admonition",{title:"Why are Layout Props Now Defined in Two Different Ways?",type:"note"},(0,o.mdx)("p",{parentName:"admonition"},"Without code-generated builders, Bloks had to make a distinction between props of the container itself and common props that can be set on children of the container."),(0,o.mdx)("p",{parentName:"admonition"},"For example, ",(0,o.mdx)("inlineCode",{parentName:"p"},"alignItems")," configures the default alignment for children in this container and is only valid on a flexbox container. It's set like a normal component prop on ",(0,o.mdx)("inlineCode",{parentName:"p"},"Row"),"/",(0,o.mdx)("inlineCode",{parentName:"p"},"Column"),"."),(0,o.mdx)("p",{parentName:"admonition"},"On the other hand, ",(0,o.mdx)("inlineCode",{parentName:"p"},"alignSelf")," can be respected on any child of a container, whether it's a Text, a Switch, or some custom Component: it's therefore exposed as a common prop via ",(0,o.mdx)("inlineCode",{parentName:"p"},"Style"),"."),(0,o.mdx)("p",{parentName:"admonition"},"The benefit of this is that Developers can now statically verify all required props are set instead of verifying at runtime (as well as not rely on Codegen!).")),(0,o.mdx)("h3",{id:"flexbox-properties-cheatsheet"},"Flexbox Properties Cheatsheet"),(0,o.mdx)("p",null,"The following table helps to identify whether to set a layout property directly on the ",(0,o.mdx)("inlineCode",{parentName:"p"},"Row"),"/",(0,o.mdx)("inlineCode",{parentName:"p"},"Column")," or on the ",(0,o.mdx)("inlineCode",{parentName:"p"},"Style"),":"),(0,o.mdx)("table",null,(0,o.mdx)("thead",{parentName:"table"},(0,o.mdx)("tr",{parentName:"thead"},(0,o.mdx)("th",{parentName:"tr",align:"left"},"Flexbox Property"),(0,o.mdx)("th",{parentName:"tr",align:"center"},"Configures a Specific Child?"),(0,o.mdx)("th",{parentName:"tr",align:"left"},"Example KComponent Usage"))),(0,o.mdx)("tbody",{parentName:"table"},(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"alignContent")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"No"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Row(alignContent = YogaAlign.CENTER)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"alignItems")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"No"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Row(alignItems = YogaAlign.CENTER)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"alignSelf")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"Yes"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Style.alignSelf(YogaAlign.CENTER)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"height")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"Yes"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Style.height(50.dp)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"flexBasis")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"Yes"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Style.flex(basis = 8.dp)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"flexBasisPercent")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"Yes"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Style.flex(basisPercent = 25f)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"flexGrow")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"Yes"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Style.flex(grow = 1f)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"flexShrink")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"Yes"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Style.flex(shrink = 1f)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"justifyContent")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"No"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Row(justifyContent = YogaJustify.CENTER)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"margin")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"Yes"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Style.margin(8.dp)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"padding")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"Yes"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Style.padding(8.dp)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"width")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"Yes"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Style.width(50.dp)"))),(0,o.mdx)("tr",{parentName:"tbody"},(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"wrap")),(0,o.mdx)("td",{parentName:"tr",align:"center"},"No"),(0,o.mdx)("td",{parentName:"tr",align:"left"},(0,o.mdx)("inlineCode",{parentName:"td"},"Row(wrap = YogaWrap.NO_WRAP)"))))),(0,o.mdx)("admonition",{type:"note"},(0,o.mdx)("p",{parentName:"admonition"},"A property is set via ",(0,o.mdx)("inlineCode",{parentName:"p"},"Style")," if and only if it configures a specific child!")),(0,o.mdx)("h3",{id:"example-migration"},"Example Migration"),(0,o.mdx)("p",null,"Below is an example of a simple ",(0,o.mdx)("inlineCode",{parentName:"p"},"Component")," using various layout properties, converted to an equivalent KComponent:"),(0,o.mdx)(l.default,{groupId:"flexbox",defaultValue:"kotlin_flexbox",values:[{label:"Kotlin",value:"kotlin_flexbox"},{label:"Java",value:"java_flexbox"}],mdxType:"Tabs"},(0,o.mdx)(i.default,{value:"kotlin_flexbox",mdxType:"TabItem"},(0,o.mdx)("pre",null,(0,o.mdx)("code",{parentName:"pre",className:"language-kotlin",metastring:"file=sample/src/main/java/com/facebook/samples/litho/kotlin/documentation/FlexboxComponent.kt start=start_example end=end_example",file:"sample/src/main/java/com/facebook/samples/litho/kotlin/documentation/FlexboxComponent.kt",start:"start_example",end:"end_example"},"class FlexboxComponent(\n    private val username: String,\n    @DrawableRes private val avatarRes: Int,\n    @DrawableRes private val imageRes: Int\n) : KComponent() {\n  override fun ComponentScope.render(): Component? {\n    return Column {\n      child(\n          Row(alignItems = YogaAlign.CENTER, style = Style.padding(all = 8.dp)) {\n            child(\n                Image(\n                    drawable = drawableRes(avatarRes),\n                    style = Style.width(36.dp).height(36.dp).margin(start = 4.dp, end = 8.dp)))\n            child(Text(text = username, textStyle = Typeface.BOLD))\n          })\n      child(\n          Image(\n              drawable = drawableRes(imageRes),\n              scaleType = ImageView.ScaleType.CENTER_CROP,\n              style = Style.aspectRatio(1f)))\n    }\n  }\n}\n"))),(0,o.mdx)(i.default,{value:"java_flexbox",mdxType:"TabItem"},(0,o.mdx)("pre",null,(0,o.mdx)("code",{parentName:"pre",className:"language-java",metastring:"file=sample/src/main/java/com/facebook/samples/litho/java/onboarding/JavaApiComponentSpec.java start=start_example end=end_example",file:"sample/src/main/java/com/facebook/samples/litho/java/onboarding/JavaApiComponentSpec.java",start:"start_example",end:"end_example"},"@LayoutSpec\npublic class JavaApiComponentSpec {\n\n  @OnCreateLayout\n  static Component onCreateLayout(\n      ComponentContext c, @Prop String username, @Prop int avatarRes, @Prop int imageRes) {\n    return Column.create(c)\n        .child(\n            Row.create(c)\n                .paddingDip(YogaEdge.ALL, 8)\n                .alignItems(YogaAlign.CENTER)\n                .child(\n                    Image.create(c)\n                        .drawableRes(avatarRes)\n                        .widthDip(36)\n                        .heightDip(36)\n                        .marginDip(YogaEdge.START, 4)\n                        .marginDip(YogaEdge.END, 8))\n                .child(Text.create(c).text(username).textStyle(Typeface.BOLD)))\n        .child(\n            Image.create(c)\n                .drawableRes(imageRes)\n                .scaleType(ImageView.ScaleType.CENTER_CROP)\n                .aspectRatio(1))\n        .build();\n  }\n}\n")))))}y.isMDXComponent=!0},23746:(e,t,n)=>{n.d(t,{ZP:()=>f,lG:()=>l});var a=n(87410);const r={plain:{backgroundColor:"#2a2734",color:"#9a86fd"},styles:[{types:["comment","prolog","doctype","cdata","punctuation"],style:{color:"#6c6783"}},{types:["namespace"],style:{opacity:.7}},{types:["tag","operator","number"],style:{color:"#e09142"}},{types:["property","function"],style:{color:"#9a86fd"}},{types:["tag-id","selector","atrule-id"],style:{color:"#eeebff"}},{types:["attr-name"],style:{color:"#c4b9fe"}},{types:["boolean","string","entity","url","attr-value","keyword","control","directive","unit","statement","regex","atrule","placeholder","variable"],style:{color:"#ffcc99"}},{types:["deleted"],style:{textDecorationLine:"line-through"}},{types:["inserted"],style:{textDecorationLine:"underline"}},{types:["italic"],style:{fontStyle:"italic"}},{types:["important","bold"],style:{fontWeight:"bold"}},{types:["important"],style:{color:"#c4b9fe"}}]};var o=n(67294),l={Prism:a.Z,theme:r};function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function p(){return p=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var a in n)Object.prototype.hasOwnProperty.call(n,a)&&(e[a]=n[a])}return e},p.apply(this,arguments)}var d=/\r\n|\r|\n/,m=function(e){0===e.length?e.push({types:["plain"],content:"\n",empty:!0}):1===e.length&&""===e[0].content&&(e[0].content="\n",e[0].empty=!0)},s=function(e,t){var n=e.length;return n>0&&e[n-1]===t?e:e.concat(t)},c=function(e,t){var n=e.plain,a=Object.create(null),r=e.styles.reduce((function(e,n){var a=n.languages,r=n.style;return a&&!a.includes(t)||n.types.forEach((function(t){var n=p({},e[t],r);e[t]=n})),e}),a);return r.root=n,r.plain=p({},n,{backgroundColor:null}),r};function u(e,t){var n={};for(var a in e)Object.prototype.hasOwnProperty.call(e,a)&&-1===t.indexOf(a)&&(n[a]=e[a]);return n}const f=function(e){function t(){for(var t=this,n=[],a=arguments.length;a--;)n[a]=arguments[a];e.apply(this,n),i(this,"getThemeDict",(function(e){if(void 0!==t.themeDict&&e.theme===t.prevTheme&&e.language===t.prevLanguage)return t.themeDict;t.prevTheme=e.theme,t.prevLanguage=e.language;var n=e.theme?c(e.theme,e.language):void 0;return t.themeDict=n})),i(this,"getLineProps",(function(e){var n=e.key,a=e.className,r=e.style,o=p({},u(e,["key","className","style","line"]),{className:"token-line",style:void 0,key:void 0}),l=t.getThemeDict(t.props);return void 0!==l&&(o.style=l.plain),void 0!==r&&(o.style=void 0!==o.style?p({},o.style,r):r),void 0!==n&&(o.key=n),a&&(o.className+=" "+a),o})),i(this,"getStyleForToken",(function(e){var n=e.types,a=e.empty,r=n.length,o=t.getThemeDict(t.props);if(void 0!==o){if(1===r&&"plain"===n[0])return a?{display:"inline-block"}:void 0;if(1===r&&!a)return o[n[0]];var l=a?{display:"inline-block"}:{},i=n.map((function(e){return o[e]}));return Object.assign.apply(Object,[l].concat(i))}})),i(this,"getTokenProps",(function(e){var n=e.key,a=e.className,r=e.style,o=e.token,l=p({},u(e,["key","className","style","token"]),{className:"token "+o.types.join(" "),children:o.content,style:t.getStyleForToken(o),key:void 0});return void 0!==r&&(l.style=void 0!==l.style?p({},l.style,r):r),void 0!==n&&(l.key=n),a&&(l.className+=" "+a),l})),i(this,"tokenize",(function(e,t,n,a){var r={code:t,grammar:n,language:a,tokens:[]};e.hooks.run("before-tokenize",r);var o=r.tokens=e.tokenize(r.code,r.grammar,r.language);return e.hooks.run("after-tokenize",r),o}))}return e&&(t.__proto__=e),t.prototype=Object.create(e&&e.prototype),t.prototype.constructor=t,t.prototype.render=function(){var e=this.props,t=e.Prism,n=e.language,a=e.code,r=e.children,o=this.getThemeDict(this.props),l=t.languages[n];return r({tokens:function(e){for(var t=[[]],n=[e],a=[0],r=[e.length],o=0,l=0,i=[],p=[i];l>-1;){for(;(o=a[l]++)<r[l];){var c=void 0,u=t[l],f=n[l][o];if("string"==typeof f?(u=l>0?u:["plain"],c=f):(u=s(u,f.type),f.alias&&(u=s(u,f.alias)),c=f.content),"string"==typeof c){var g=c.split(d),y=g.length;i.push({types:u,content:g[0]});for(var x=1;x<y;x++)m(i),p.push(i=[]),i.push({types:u,content:g[x]})}else l++,t.push(u),n.push(c),a.push(0),r.push(c.length)}l--,t.pop(),n.pop(),a.pop(),r.pop()}return m(i),p}(void 0!==l?this.tokenize(t,a,l,n):[a]),className:"prism-code language-"+n,style:void 0!==o?o.root:{},getLineProps:this.getLineProps,getTokenProps:this.getTokenProps})},t}(o.Component)},13618:(e,t,n)=>{n.d(t,{Z:()=>a});const a={plain:{color:"#F8F8F2",backgroundColor:"#282A36"},styles:[{types:["prolog","constant","builtin"],style:{color:"rgb(189, 147, 249)"}},{types:["inserted","function"],style:{color:"rgb(80, 250, 123)"}},{types:["deleted"],style:{color:"rgb(255, 85, 85)"}},{types:["changed"],style:{color:"rgb(255, 184, 108)"}},{types:["punctuation","symbol"],style:{color:"rgb(248, 248, 242)"}},{types:["string","char","tag","selector"],style:{color:"rgb(255, 121, 198)"}},{types:["keyword","variable"],style:{color:"rgb(189, 147, 249)",fontStyle:"italic"}},{types:["comment"],style:{color:"rgb(98, 114, 164)"}},{types:["attr-name"],style:{color:"rgb(241, 250, 140)"}}]}},7694:(e,t,n)=>{n.d(t,{Z:()=>a});const a={plain:{color:"#393A34",backgroundColor:"#f6f8fa"},styles:[{types:["comment","prolog","doctype","cdata"],style:{color:"#999988",fontStyle:"italic"}},{types:["namespace"],style:{opacity:.7}},{types:["string","attr-value"],style:{color:"#e3116c"}},{types:["punctuation","operator"],style:{color:"#393A34"}},{types:["entity","url","symbol","number","boolean","variable","constant","property","regex","inserted"],style:{color:"#36acaa"}},{types:["atrule","keyword","attr-name","selector"],style:{color:"#00a4db"}},{types:["function","deleted","tag"],style:{color:"#d73a49"}},{types:["function-variable"],style:{color:"#6f42c1"}},{types:["tag","selector","keyword"],style:{color:"#00009f"}}]}}}]);