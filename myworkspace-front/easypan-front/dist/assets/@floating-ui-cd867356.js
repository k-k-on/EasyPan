function Y(t){return t.split("-")[1]}function ot(t){return t==="y"?"height":"width"}function _(t){return t.split("-")[0]}function j(t){return["top","bottom"].includes(_(t))?"x":"y"}function K(t,e,o){let{reference:n,floating:i}=t;const r=n.x+n.width/2-i.width/2,l=n.y+n.height/2-i.height/2,c=j(e),f=ot(c),s=n[f]/2-i[f]/2,p=c==="x";let a;switch(_(e)){case"top":a={x:r,y:n.y-i.height};break;case"bottom":a={x:r,y:n.y+n.height};break;case"right":a={x:n.x+n.width,y:l};break;case"left":a={x:n.x-i.width,y:l};break;default:a={x:n.x,y:n.y}}switch(Y(e)){case"start":a[c]-=s*(o&&p?-1:1);break;case"end":a[c]+=s*(o&&p?-1:1)}return a}const mt=async(t,e,o)=>{const{placement:n="bottom",strategy:i="absolute",middleware:r=[],platform:l}=o,c=r.filter(Boolean),f=await(l.isRTL==null?void 0:l.isRTL(e));let s=await l.getElementRects({reference:t,floating:e,strategy:i}),{x:p,y:a}=K(s,n,f),u=n,d={},y=0;for(let h=0;h<c.length;h++){const{name:g,fn:m}=c[h],{x:w,y:R,data:V,reset:b}=await m({x:p,y:a,initialPlacement:n,placement:u,strategy:i,middlewareData:d,rects:s,platform:l,elements:{reference:t,floating:e}});p=w??p,a=R??a,d={...d,[g]:{...d[g],...V}},b&&y<=50&&(y++,typeof b=="object"&&(b.placement&&(u=b.placement),b.rects&&(s=b.rects===!0?await l.getElementRects({reference:t,floating:e,strategy:i}):b.rects),{x:p,y:a}=K(s,u,f)),h=-1)}return{x:p,y:a,placement:u,strategy:i,middlewareData:d}};function it(t,e){return typeof t=="function"?t(e):t}function xt(t){return typeof t!="number"?function(e){return{top:0,right:0,bottom:0,left:0,...e}}(t):{top:t,right:t,bottom:t,left:t}}function rt(t){return{...t,top:t.y,left:t.x,right:t.x+t.width,bottom:t.y+t.height}}const N=Math.min,wt=Math.max;function bt(t,e,o){return wt(t,N(e,o))}const Ct=t=>({name:"arrow",options:t,async fn(e){const{x:o,y:n,placement:i,rects:r,platform:l,elements:c}=e,{element:f,padding:s=0}=it(t,e)||{};if(f==null)return{};const p=xt(s),a={x:o,y:n},u=j(i),d=ot(u),y=await l.getDimensions(f),h=u==="y",g=h?"top":"left",m=h?"bottom":"right",w=h?"clientHeight":"clientWidth",R=r.reference[d]+r.reference[u]-a[u]-r.floating[d],V=a[u]-r.reference[u],b=await(l.getOffsetParent==null?void 0:l.getOffsetParent(f));let $=b?b[w]:0;$&&await(l.isElement==null?void 0:l.isElement(b))||($=c.floating[w]||r.floating[d]);const yt=R/2-V/2,z=$/2-y[d]/2-1,G=N(p[g],z),I=N(p[m],z),F=G,J=$-y[d]-I,S=$/2-y[d]/2+yt,B=bt(F,S,J),gt=Y(i)!=null&&S!=B&&r.reference[d]/2-(S<F?G:I)-y[d]/2<0;return{[u]:a[u]-(gt?S<F?F-S:J-S:0),data:{[u]:B,centerOffset:S-B}}}}),vt=["top","right","bottom","left"];vt.reduce((t,e)=>t.concat(e,e+"-start",e+"-end"),[]);const Et=function(t){return t===void 0&&(t=0),{name:"offset",options:t,async fn(e){const{x:o,y:n}=e,i=await async function(r,l){const{placement:c,platform:f,elements:s}=r,p=await(f.isRTL==null?void 0:f.isRTL(s.floating)),a=_(c),u=Y(c),d=j(c)==="x",y=["left","top"].includes(a)?-1:1,h=p&&d?-1:1,g=it(l,r);let{mainAxis:m,crossAxis:w,alignmentAxis:R}=typeof g=="number"?{mainAxis:g,crossAxis:0,alignmentAxis:null}:{mainAxis:0,crossAxis:0,alignmentAxis:null,...g};return u&&typeof R=="number"&&(w=u==="end"?-1*R:R),d?{x:w*h,y:m*y}:{x:m*y,y:w*h}}(e,t);return{x:o+i.x,y:n+i.y,data:i}}}};function x(t){var e;return((e=t.ownerDocument)==null?void 0:e.defaultView)||window}function v(t){return x(t).getComputedStyle(t)}function lt(t){return t instanceof x(t).Node}function E(t){return lt(t)?(t.nodeName||"").toLowerCase():""}function L(t){return t instanceof x(t).HTMLElement}function T(t){return t instanceof x(t).Element}function Q(t){return typeof ShadowRoot>"u"?!1:t instanceof x(t).ShadowRoot||t instanceof ShadowRoot}function W(t){const{overflow:e,overflowX:o,overflowY:n,display:i}=v(t);return/auto|scroll|overlay|hidden|clip/.test(e+n+o)&&!["inline","contents"].includes(i)}function Lt(t){return["table","td","th"].includes(E(t))}function X(t){const e=q(),o=v(t);return o.transform!=="none"||o.perspective!=="none"||!e&&!!o.backdropFilter&&o.backdropFilter!=="none"||!e&&!!o.filter&&o.filter!=="none"||["transform","perspective","filter"].some(n=>(o.willChange||"").includes(n))||["paint","layout","strict","content"].some(n=>(o.contain||"").includes(n))}function q(){return!(typeof CSS>"u"||!CSS.supports)&&CSS.supports("-webkit-backdrop-filter","none")}function P(t){return["html","body","#document"].includes(E(t))}const U=Math.min,A=Math.max,O=Math.round;function ct(t){const e=v(t);let o=parseFloat(e.width)||0,n=parseFloat(e.height)||0;const i=L(t),r=i?t.offsetWidth:o,l=i?t.offsetHeight:n,c=O(o)!==r||O(n)!==l;return c&&(o=r,n=l),{width:o,height:n,fallback:c}}function st(t){return T(t)?t:t.contextElement}const ft={x:1,y:1};function k(t){const e=st(t);if(!L(e))return ft;const o=e.getBoundingClientRect(),{width:n,height:i,fallback:r}=ct(e);let l=(r?O(o.width):o.width)/n,c=(r?O(o.height):o.height)/i;return l&&Number.isFinite(l)||(l=1),c&&Number.isFinite(c)||(c=1),{x:l,y:c}}const Z={x:0,y:0};function at(t,e,o){var n,i;if(e===void 0&&(e=!0),!q())return Z;const r=t?x(t):window;return!o||e&&o!==r?Z:{x:((n=r.visualViewport)==null?void 0:n.offsetLeft)||0,y:((i=r.visualViewport)==null?void 0:i.offsetTop)||0}}function H(t,e,o,n){e===void 0&&(e=!1),o===void 0&&(o=!1);const i=t.getBoundingClientRect(),r=st(t);let l=ft;e&&(n?T(n)&&(l=k(n)):l=k(t));const c=at(r,o,n);let f=(i.left+c.x)/l.x,s=(i.top+c.y)/l.y,p=i.width/l.x,a=i.height/l.y;if(r){const u=x(r),d=n&&T(n)?x(n):n;let y=u.frameElement;for(;y&&n&&d!==u;){const h=k(y),g=y.getBoundingClientRect(),m=getComputedStyle(y);g.x+=(y.clientLeft+parseFloat(m.paddingLeft))*h.x,g.y+=(y.clientTop+parseFloat(m.paddingTop))*h.y,f*=h.x,s*=h.y,p*=h.x,a*=h.y,f+=g.x,s+=g.y,y=x(y).frameElement}}return rt({width:p,height:a,x:f,y:s})}function C(t){return((lt(t)?t.ownerDocument:t.document)||window.document).documentElement}function M(t){return T(t)?{scrollLeft:t.scrollLeft,scrollTop:t.scrollTop}:{scrollLeft:t.pageXOffset,scrollTop:t.pageYOffset}}function ut(t){return H(C(t)).left+M(t).scrollLeft}function D(t){if(E(t)==="html")return t;const e=t.assignedSlot||t.parentNode||Q(t)&&t.host||C(t);return Q(e)?e.host:e}function dt(t){const e=D(t);return P(e)?e.ownerDocument.body:L(e)&&W(e)?e:dt(e)}function ht(t,e){var o;e===void 0&&(e=[]);const n=dt(t),i=n===((o=t.ownerDocument)==null?void 0:o.body),r=x(n);return i?e.concat(r,r.visualViewport||[],W(n)?n:[]):e.concat(n,ht(n))}function tt(t,e,o){let n;if(e==="viewport")n=function(i,r){const l=x(i),c=C(i),f=l.visualViewport;let s=c.clientWidth,p=c.clientHeight,a=0,u=0;if(f){s=f.width,p=f.height;const d=q();(!d||d&&r==="fixed")&&(a=f.offsetLeft,u=f.offsetTop)}return{width:s,height:p,x:a,y:u}}(t,o);else if(e==="document")n=function(i){const r=C(i),l=M(i),c=i.ownerDocument.body,f=A(r.scrollWidth,r.clientWidth,c.scrollWidth,c.clientWidth),s=A(r.scrollHeight,r.clientHeight,c.scrollHeight,c.clientHeight);let p=-l.scrollLeft+ut(i);const a=-l.scrollTop;return v(c).direction==="rtl"&&(p+=A(r.clientWidth,c.clientWidth)-f),{width:f,height:s,x:p,y:a}}(C(t));else if(T(e))n=function(i,r){const l=H(i,!0,r==="fixed"),c=l.top+i.clientTop,f=l.left+i.clientLeft,s=L(i)?k(i):{x:1,y:1};return{width:i.clientWidth*s.x,height:i.clientHeight*s.y,x:f*s.x,y:c*s.y}}(e,o);else{const i=at(t);n={...e,x:e.x-i.x,y:e.y-i.y}}return rt(n)}function pt(t,e){const o=D(t);return!(o===e||!T(o)||P(o))&&(v(o).position==="fixed"||pt(o,e))}function et(t,e){return L(t)&&v(t).position!=="fixed"?e?e(t):t.offsetParent:null}function nt(t,e){const o=x(t);if(!L(t))return o;let n=et(t,e);for(;n&&Lt(n)&&v(n).position==="static";)n=et(n,e);return n&&(E(n)==="html"||E(n)==="body"&&v(n).position==="static"&&!X(n))?o:n||function(i){let r=D(i);for(;L(r)&&!P(r);){if(X(r))return r;r=D(r)}return null}(t)||o}function Tt(t,e,o){const n=L(e),i=C(e),r=o==="fixed",l=H(t,!0,r,e);let c={scrollLeft:0,scrollTop:0};const f={x:0,y:0};if(n||!n&&!r)if((E(e)!=="body"||W(i))&&(c=M(e)),L(e)){const s=H(e,!0,r,e);f.x=s.x+e.clientLeft,f.y=s.y+e.clientTop}else i&&(f.x=ut(i));return{x:l.left+c.scrollLeft-f.x,y:l.top+c.scrollTop-f.y,width:l.width,height:l.height}}const Rt={getClippingRect:function(t){let{element:e,boundary:o,rootBoundary:n,strategy:i}=t;const r=o==="clippingAncestors"?function(s,p){const a=p.get(s);if(a)return a;let u=ht(s).filter(g=>T(g)&&E(g)!=="body"),d=null;const y=v(s).position==="fixed";let h=y?D(s):s;for(;T(h)&&!P(h);){const g=v(h),m=X(h);m||g.position!=="fixed"||(d=null),(y?!m&&!d:!m&&g.position==="static"&&d&&["absolute","fixed"].includes(d.position)||W(h)&&!m&&pt(s,h))?u=u.filter(w=>w!==h):d=g,h=D(h)}return p.set(s,u),u}(e,this._c):[].concat(o),l=[...r,n],c=l[0],f=l.reduce((s,p)=>{const a=tt(e,p,i);return s.top=A(a.top,s.top),s.right=U(a.right,s.right),s.bottom=U(a.bottom,s.bottom),s.left=A(a.left,s.left),s},tt(e,c,i));return{width:f.right-f.left,height:f.bottom-f.top,x:f.left,y:f.top}},convertOffsetParentRelativeRectToViewportRelativeRect:function(t){let{rect:e,offsetParent:o,strategy:n}=t;const i=L(o),r=C(o);if(o===r)return e;let l={scrollLeft:0,scrollTop:0},c={x:1,y:1};const f={x:0,y:0};if((i||!i&&n!=="fixed")&&((E(o)!=="body"||W(r))&&(l=M(o)),L(o))){const s=H(o);c=k(o),f.x=s.x+o.clientLeft,f.y=s.y+o.clientTop}return{width:e.width*c.x,height:e.height*c.y,x:e.x*c.x-l.scrollLeft*c.x+f.x,y:e.y*c.y-l.scrollTop*c.y+f.y}},isElement:T,getDimensions:function(t){return ct(t)},getOffsetParent:nt,getDocumentElement:C,getScale:k,async getElementRects(t){let{reference:e,floating:o,strategy:n}=t;const i=this.getOffsetParent||nt,r=this.getDimensions;return{reference:Tt(e,await i(o),n),floating:{x:0,y:0,...await r(o)}}},getClientRects:t=>Array.from(t.getClientRects()),isRTL:t=>v(t).direction==="rtl"},St=(t,e,o)=>{const n=new Map,i={platform:Rt,...o},r={...i.platform,_c:n};return mt(t,e,{...i,platform:r})};export{Et as L,St as M,Ct as g};
