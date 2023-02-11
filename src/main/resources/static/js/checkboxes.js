// Utility functions

function checkChildren(checkbox, checked) {
    let node = checkbox.tagName === 'INPUT' ? checkbox.parentNode : checkbox;
    node.childNodes.forEach(function(child) {
        if (child.tagName === 'INPUT') {
            child.checked = checked;
        } else {
            checkChildren(child, checked);
        }
    });
}

function checkChildrenOn(checkbox) {
    var allChecked = true;
    let node = checkbox.tagName === 'INPUT' ? checkbox.parentNode : checkbox;
    node.childNodes.forEach(function(child) {
        if (child.tagName === 'INPUT') {
            allChecked = allChecked && (child.checked || child.id == checkbox.id);
        } else {
            allChecked = allChecked && checkChildrenOn(child);
        }
    });
    return allChecked;
}

function checkParents(checkbox) {
    let node = checkbox.parentNode.parentNode;
    var parentFound = false;
    node.childNodes.forEach(function(child) {
        if (child.tagName === 'INPUT') {
            child.checked = checkChildrenOn(child);
            checkParents(child);
            parentFound = true;
        }
    });
    if (!parentFound) {
        node.parentNode.parentNode.childNodes.forEach(function(child) {
            if (child.tagName === 'INPUT') {
                child.checked = checkChildrenOn(child);
                checkParents(child);
            }
        });
    }
}

function checkForId(id) {
    let element = document.getElementById(id)
    element.checked = true;
    checkChildren(element, element.checked);
    checkParents(element);
}

// Apply to checkboxes

var checkboxes = Array.from(document.getElementsByTagName('input')).filter(function(input) {
    return input.type === 'checkbox';
});

checkboxes.forEach(function(checkbox) {
    checkbox.addEventListener('click', function(event) {
        checkChildren(checkbox, checkbox.checked);
    });
    checkbox.addEventListener('change', function(event) {
        checkParents(checkbox);
    });
});
