function switchView(Id) {
	var e = document.getElementById(Id);
	if (e.style.display == "block") {
		e.style.display = "none";
	} else {
		e.style.display = "block";
	}
}

function addToBlock(id, block) {
	var elm = document.getElementById(id);
	elm.insertAdjacentHTML("beforeend", block);
}

function deleteBlock(id) {
	var elm = document.getElementById(id);
	elm.parentNode.removeChild(elm);
}
