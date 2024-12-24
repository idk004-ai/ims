(function () {
	"use strict";

	var treeviewMenu = $('.app-menu');

	// Toggle Sidebar
	$('[data-toggle="sidebar"]').click(function(event) {
		event.preventDefault();
		$('.app').toggleClass('sidenav-toggled');
	});

	// Activate sidebar treeview toggle
	$("[data-toggle='treeview']").click(function(event) {
		event.preventDefault();
		if(!$(this).parent().hasClass('is-expanded')) {
			treeviewMenu.find("[data-toggle='treeview']").parent().removeClass('is-expanded');
		}
		$(this).parent().toggleClass('is-expanded');
	});

	// Set initial active toggle
	$("[data-toggle='treeview.'].is-expanded").parent().toggleClass('is-expanded');

	//Activate bootstrip tooltips
	// $("[data-toggle='tooltip']").tooltip();

});
document.addEventListener('DOMContentLoaded', () => {

    const currentPath = window.location.pathname;

    const menuMap = {
        '/api/v1/home': 'menu-home',
        '/api/v1/candidates': 'menu-candidates',
        '/api/v1/jobs': 'menu-jobs',
        '/api/v1/interview': 'menu-interview',
        '/api/v1/offer': 'menu-offer',
        '/api/v1/user': 'menu-user',
    };

    for (const [path, menuId] of Object.entries(menuMap)) {
        if (currentPath.includes(path)) {
            const activeMenuItem = document.getElementById(menuId);
            activeMenuItem?.classList.add('active');
            break;
        }
    }
});

