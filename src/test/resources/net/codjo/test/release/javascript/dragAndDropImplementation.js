/**
 * dragOn
 * =============================================================================
 * permet de glisser-déposer des éléments html
 *
 * @author      Erwan Lefèvre <erwan.lefevre@gmail.com>
 * @copyright   Erwan Lefèvre 2009
 * @license     Creative Commons - Paternité 2.0 France - http://creativecommons.org/licenses/by/2.0/fr/
 * @version     2.1 | 2010-08-12
 * @see			http://www.webbricks.org/bricks/dragOn/
 *
 * @compatible	au 12 août 2010, compatibilité assurée pour :
 *				Firefox 1.5+, Internet Explorer 5+, Opéra 10, Safari 3.2.3.
 *				Autres versions et navigateurs non testés
 */



/* exemples

-	version basique :

	dragOn.apply(getElementById('draggable')); // permet de déplacer l'élément dont l'attribut id est "draggable"


-	avec options :

	dragOn.apply ( getElementById('draggable'),
		{
			handle : document.getElementById('handle'),		// l'élément par où saisir "draggable" (la poignée, si on veut)
			movingClass : 'isMoving',						// la classe css à ajouter à "draggable" quand on le déplace
			cssPosition : 'fixed',							// indique la position css initiale de "draggable" (utile pour 'fixed' uniquement)
			moveArea : document.getElementById('area'),		// l'élément draggé ne pourra être déplacé hors de la surface de l'élément 'area'
			moveHoriz : false								// l'élément draggé ne pourra pas être déplacé à l'horizontale
		}
	);

*/






/**
 * addEvent()
 *
 * ajoute la fonction /fn/ à la pile de résolution de l'événement /evenType/ de
 * l'objet /obj/
 *
 * merci à : http://www.scottandrew.com/weblog/articles/cbs-events
 *
 * @param		{Mixed}				obj			window, ou document, ou un élément HTML
 * @param		{String}			evType		type d'event (click, mouseover, mouseout, etc.)
 * * @param		{String}			fn			la fonction à ajouter
 * @param		{Boolean}			useCapture	"useCapture un booléen : true pour la phase de capture, ou false pour la phase de bouillonnement et la cible. On utilise quasiment toujours la valeur false."
 *
 * @returns		void
 *
 * -----------------------------------------------------------------------------
 */
function addEvent (obj, evType, fn, useCapture){
	if (obj.addEventListener) { obj.addEventListener(evType, fn, useCapture); }
	else { obj.attachEvent("on"+evType, fn); }
}





/**
 * getPos()
 * =============================================================================
 * retourne la position (dans la page) de chacun des côtés de l'élément /elem/,
 * dispatché dans un tableau associatif contenant les clés t|b|l|r
 * (la valeur retournée est donnée en pixels)
 * (tient compte des différences de fonctionnement des navigateur)
 *
 * @param           Object          elem            l'élément inspecté
 * @return          Integer
 * @access          public
 */
function getPos(elem) {
    var pos={'r':0,'l':0,'t':0,'b':0};
    var tmp=elem;

    // on procède de parent en parent car IE fonctionne comme ça
    // (les autres donnent directement la position par rapport à la page)

    do {
        pos.l += tmp.offsetLeft;
        tmp = tmp.offsetParent;
    } while( tmp !== null );
    pos.r = pos.l + elem.offsetWidth;

    tmp=elem;
    do {
        pos.t += tmp.offsetTop;
        tmp = tmp.offsetParent;
    } while( tmp !== null );
    pos.b = pos.t + elem.offsetHeight;

    return pos;
}





/**
 * mousePos()
 * =============================================================================
 * retourne un tableau {'x','y'} des positions de la souris dans la page
 *
 * @return		void
 * @param		event		e		l'événement auquel on attribue cette f°
 *
 */
var mousePos = {'x':0,'y':0};
function getMousePos(e) {
	var d = document,
		de = d.documentElement,
		db = document.body;

	e = e || window.event;
	if (e.pageX || e.pageY) {
		mousePos.x = e.pageX;
		mousePos.y = e.pageY;
	}
	else if (e.clientX || e.clientY) {
		mousePos.x = e.clientX + db.scrollLeft + de.scrollLeft;
		mousePos.y = e.clientY + db.scrollTop + de.scrollTop;
	}
}
/**
 * mise en place du relevé de coordonées de la souris, en cas de déplacement de la souris
 */
addEvent(document, 'mousemove', getMousePos);





/** inRange()
 * -----------------------------------------------------------------------------
 * retourne true si le vecteur /aMin/-/aMax/ recouvre le vecteur /bMin/-/bMax/, sinon false
 *
 * @param           Float          aMin            valeur minimale du vecteur a
 * @param           Float          aMax            valeur maximale du vecteur a
 * @param           Float          bMin            valeur minimale du vecteur b
 * @param           Float          bMax            valeur maximale du vecteur b
 *
 * @return          Boolean
 */

function inRange(aMin, aMax, bMin, bMax) {
    if ( ( (aMin<=bMax) && (aMin>=bMin) ) || ( (aMax<=bMax) && (aMax>=bMin) ) ) { return true; }
    return false ;
}






/** isOver()
 * -----------------------------------------------------------------------------
 * retourne true si l'objet /a/ recouvre au moins une partie de l'objet b, sinon false
 *
 * @param           Object          a            l'objet couvrant
 * @param           Object          b            l'objet couvert
 *
 * @return          Boolean
 */
function isOver(a, b) {
    var posA = getPos(a),
        posB = getPos(b),
        aTop, aBottom, aLeft, aRight,
        bTop, bBottom, bLeft, bRight;

    aTop    = posA.t;
    aBottom = posA.b;
    aLeft   = posA.l;
    aRight  = posA.r;
    bTop    = posB.t;
    bBottom = posB.b;
    bLeft   = posB.l;
    bRight  = posB.r;

    if ( inRange(aTop, aBottom, bTop, bBottom) && inRange(aLeft, aRight, bLeft, bRight) ) { return true; }

    return false;
}






/**
 * dragOn v2.1 | 2010-08-12
 * =============================================================================
 * permet de glisser-déposer des éléments html
 *
 * @requires	getPos
 * @requires	inRange		uniquement pour les contraintes de déplacement
 * @requires	isOver		uniquement pour les contraintes de déplacement
 * @requires	mousePos
 * @requires	addEvent
 *
 */
var dragOn = {

	decalX : 0, // mémorise le décalage horizontal entre la souris et l'élément dragué
	decalY : 0, // mémorise le décalage vertical entre la souris et l'élément dragué
	isDragging : 0, // mémorise l'élément en train d'être dragué
	maxZ : 0, // z-index de l'élément le plus proche (le dernier qu'on a avancé)



	/**
	 * dragOn.before()
	 *
	 * passer un élément html au premier plan (par défaut : l'élément dragué)
	 *
	 * @param		htmlElement		elem		l'élément html passer au premier plan (par défaut : l'élément dragué)
	 * @returns		void
	 * -------------------------------------------------------------------------
	 */
	before : function (elem) {
		elem = elem || this.isDragging;
		this.maxZ ++ ;
		elem.style.zIndex = this.maxZ;

		// gestion des contraintes min/max par zone-élément
		if (elem.dragOptions.moveArea) {
			var area = typeof elem.dragOptions.moveArea=='object' ? elem.dragOptions.moveArea : elem.dragOptions.moveArea.parentNode;
			area = getPos(area);
			elem.dragOptions.minX = area.l;
			elem.dragOptions.maxX = area.r;
			elem.dragOptions.minY = area.t;
			elem.dragOptions.maxY = area.b;
		}
	},



	/**
	 * dragOn.start()
	 *
	 * activer un dragging
	 *
	 * @param		htmlElement		elem		l'élément html à draguer
	 * @returns		void
	 * -------------------------------------------------------------------------
	 */
	start : function (elem) {
		// gestion des contraintes min/max par zone-élément
		if (elem.dragOptions.moveArea) {
			var area = typeof elem.dragOptions.moveArea=='object' ? elem.dragOptions.moveArea : elem.dragOptions.moveArea.parentNode;
			area = getPos(area);
			elem.dragOptions.minX = area.l;
			elem.dragOptions.maxX = area.r;
			elem.dragOptions.minY = area.t;
			elem.dragOptions.maxY = area.b;
		}

		// relevé de l'élément dragué
		this.isDragging = elem;

		// relevé initial de la position de l'élément draggué
		elem.style.top=getPos(elem).t+'px';
		elem.style.left=getPos(elem).l+'px';

		// s'il a une position autre que fixed, la passer en absolute
		if (elem.dragOptions.cssPosition!=='fixed') { elem.style.position='absolute'; }

		// si définie en options, donner une classe css à l'élément
		if (elem.dragOptions.movingClass) { elem.className+=" "+elem.dragOptions.movingClass; }

		// calcul de l'écart avec le curseur
		dragOn.decalX = mousePos.x - getPos(elem).l;
		dragOn.decalY = mousePos.y - getPos(elem).t;

		// gestion du zindex
		this.before(elem); // seul ie6 me force à indiquer elem ici
		this.move();
	},



	/**
	 * dragOn.stop()
	 *
	 * arrêter un dragging (correspond à un drop non ciblé)
	 *
	 * @returns		void
	 * -------------------------------------------------------------------------
	 */
	stop : function () {
		var elem = dragOn.isDragging;
		if (elem) {

			// si définie en options, retirer la classe css de dragage
			if (elem.dragOptions.movingClass) { elem.className = elem.className.replace(" "+elem.dragOptions.movingClass,''); }

			// ne plus considérer d'élément à draguer
			dragOn.isDragging = 0;
		}
	},



	/**
	 * dragOn.apply()
	 *
	 * appliquer une capacité de draguage à un élément html
	 *
	 * @param		htmlElement		target		l'élément html à draguer
	 * @param		object			otions		liste des options pour l'élément à draguer :
	 *												-	htmlElement		handle				l'éléménet html qui servira de poignée (par défaut : target)
	 *												-	boolean			cssPosition			indique la position css initiale de target (pour les position statiques en particulier)
	 *												-	string			movingClass			nom de classe css à donner à target qd on le déplace
	 * @returns		void
	 * -------------------------------------------------------------------------
	 */
	apply : function (target, options) {
		// options par défaut
		options = options || {};
		var handle = options.handle = options.handle ? options.handle : target ;
		options.cssPosition = options.cssPosition ? options.cssPosition : target.style.position ;
		options.moveHoriz = options.moveHoriz===undefined ? 1 : options.moveHoriz;
		options.moveVert = options.moveVert===undefined ? 1 : options.moveVert;

		// mémorisation des options
		target.dragOptions = options;

		// figer la taille de l'élément dragué
		target.style.width=target.clientWidth+'px';
		target.style.height=target.clientHeight+'px';

		// sur le handle, créer les événement de mise en route/arrêt du drag
		var on,moveFisrt;

		on = function (e) { dragOn.start(target); };
		addEvent(handle, 'mousedown', on);

		moveFisrt = function () { dragOn.before(target); };
		addEvent(target, 'mousedown', moveFisrt);

		// empêcher la sélection pendant le déplacement
		handle.onselectstart = function () { return false; }; // ie
		handle.onmousedown = function () { return false; }; // mozilla
	},



	/**
	 * dragOn.move()
	 *
	 * déplacement de l'élément dragué
	 *
	 * @returns		void
	 * -------------------------------------------------------------------------
	 */
	move : function () {
		var elem = dragOn.isDragging,
			opt, // raccourci de elem.dragOptions
			left, top; // positions horizontale et verticale
		if (elem) {
//			si un élément à draguer est défini
			if (elem) { // le déplacer
				// raccourci
				opt = elem.dragOptions;

				// position horizontale
				if (opt.moveHoriz) {
					left = mousePos.x - dragOn.decalX;
					left = opt.maxX!==undefined && opt.maxX<left+elem.offsetWidth ? opt.maxX-elem.offsetWidth : left;
					left = opt.minX!==undefined && opt.minX>left ? opt.minX : left;
					elem.style.left = left + "px" ;
				}

				// position verticale
				if (opt.moveVert) {
					top = mousePos.y - dragOn.decalY;
					top = opt.maxY!==undefined && opt.maxY<top+elem.offsetHeight ? opt.maxY-elem.offsetHeight : top;
					top = opt.minY!==undefined && opt.minY>top ? opt.minY : top;
					elem.style.top = top + "px" ;
				}
			}
		}
	}
};


/* mise ne place de l'effet */
addEvent(document, 'mouseup', dragOn.stop);
addEvent(document, 'mousemove', dragOn.move);