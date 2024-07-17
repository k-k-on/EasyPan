import{u as V,b as M}from"./vue-router-3dc4a115.js";import{_ as j}from"./index-23b7f611.js";import{j as E,r as g,ag as v,o as a,c as d,a as s,V as h,P as u,T as k,O as y,F as C,S as f,U as I,u as P,bh as w,bf as U}from"./@vue-7298a6f3.js";import"./element-plus-746218ca.js";import"./aplayer-585df14e.js";import"./lodash-es-36eb724a.js";import"./@vueuse-896144d5.js";import"./@element-plus-925c7e21.js";import"./@popperjs-c75af06c.js";import"./@ctrl-f8748455.js";import"./dayjs-9f3e7f81.js";import"./async-validator-10c6301d.js";import"./memoize-one-297ddbcb.js";import"./escape-html-9626137c.js";import"./normalize-wheel-es-ed76fb12.js";import"./@floating-ui-0225a2a3.js";import"./vue-cookies-fc2d14d9.js";import"./@highlightjs-600d0386.js";import"./highlight.js-6845818c.js";import"./axios-4a70c6fc.js";import"./docx-preview-8cc7001d.js";import"./jszip-1f4bc9a3.js";import"./xlsx-04f2268f.js";import"./vue-pdf-embed-4bdf9d28.js";import"./vue-d7dd0a62.js";import"./dplayer-d4a41b2a.js";import"./vue-clipboard3-69020072.js";import"./clipboard-0f4a3daa.js";const T=p=>(w("data-v-25de2ecd"),p=p(),U(),p),H={class:"top"},A=T(()=>s("span",{class:"iconfont icon-revert"},null,-1)),G=T(()=>s("span",{class:"iconfont icon-del"},null,-1)),J={class:"file-list"},K=["onMouseenter","onMouseleave"],Q=["title"],W={class:"op"},X=["onClick"],Y=["onClick"],Z={key:0},ee={__name:"Recycle",emits:["reload"],setup(p,{emit:S}){const{proxy:l}=E();V(),M();const n={loadDataList:"/recycle/loadRecycleList",delFile:"/recycle/delFile",recoverFile:"/recycle/recoverFile"},N=[{label:"文件名",prop:"fileName",scopedSlots:"fileName"},{label:"删除时间",prop:"recoveryTime",width:200},{label:"大小",prop:"fileSize",scopedSlots:"fileSize",width:200}],c=g({}),F={extHeight:20,selectType:"checkbox"},r=async()=>{let e={pageNo:c.value.pageNo,pageSize:c.value.pageSize};e.category!=="all"&&delete e.filePid;let o=await l.Request({url:n.loadDataList,params:e});o&&(c.value=o.data)},R=e=>{c.value.list.forEach(o=>{o.showOp=!1}),e.showOp=!0},x=e=>{e.showOp=!1},i=g([]),z=e=>{i.value=[],e.forEach(o=>{i.value.push(o.fileId)})},O=e=>{l.Confirm(`你确定要还原【${e.fileName}】吗？`,async()=>{await l.Request({url:n.recoverFile,params:{fileIds:e.fileId}})&&r()})},B=()=>{i.value.length!=0&&l.Confirm("你确定要还原这些文件吗？",async()=>{await l.Request({url:n.recoverFile,params:{fileIds:i.value.join(",")}})&&r()})},$=e=>{l.Confirm(`你确定要删除【${e.fileName}】？`,async()=>{await l.Request({url:n.delFile,params:{fileIds:e.fileId}})&&(r(),S("reload"))})},q=e=>{i.value.length!=0&&l.Confirm("你确定要删除选中的文件?删除将无法恢复",async()=>{await l.Request({url:n.delFile,params:{fileIds:i.value.join(",")}})&&(r(),S("reload"))})};return(e,o)=>{const b=v("el-button"),m=v("icon"),D=v("Table");return a(),d("div",null,[s("div",H,[h(b,{type:"success",disabled:i.value.length==0,onClick:B},{default:u(()=>[A,k("还原 ")]),_:1},8,["disabled"]),h(b,{type:"danger",disabled:i.value.length==0,onClick:q},{default:u(()=>[G,k("批量删除 ")]),_:1},8,["disabled"])]),s("div",J,[h(D,{columns:N,showPagination:!0,dataSource:c.value,fetch:r,options:F,onRowSelected:z},{fileName:u(({index:L,row:t})=>[s("div",{class:"file-item",onMouseenter:_=>R(t),onMouseleave:_=>x(t)},[(t.fileType==3||t.fileType==1)&&t.status!==0?(a(),y(m,{key:0,cover:t.fileCover},null,8,["cover"])):(a(),d(C,{key:1},[t.folderType==0?(a(),y(m,{key:0,fileType:t.fileType},null,8,["fileType"])):f("",!0),t.folderType==1?(a(),y(m,{key:1,fileType:0})):f("",!0)],64)),s("span",{class:"file-name",title:t.fileName},[s("span",null,I(t.fileName),1)],8,Q),s("span",W,[t.showOp&&t.fileId?(a(),d(C,{key:0},[s("span",{class:"iconfont icon-revert",onClick:_=>O(t)},"还原",8,X),s("span",{class:"iconfont icon-del",onClick:_=>$(t)},"删除",8,Y)],64)):f("",!0)])],40,K)]),fileSize:u(({index:L,row:t})=>[t.fileSize?(a(),d("span",Z,I(P(l).Utils.size2Str(t.fileSize)),1)):f("",!0)]),_:1},8,["dataSource"])])])}}},xe=j(ee,[["__scopeId","data-v-25de2ecd"]]);export{xe as default};
