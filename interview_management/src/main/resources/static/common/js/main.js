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
    // Lấy URL hiện tại
    const currentPath = window.location.pathname;

    // Liệt kê các id tương ứng với đường dẫn
    const menuMap = {
        '/api/v1/home': 'menu-home',
        '/api/v1/candidates/list_candidates': 'menu-candidates',
        '/api/v1/jobs/list-job': 'menu-jobs',
        '/api/v1/interview': 'menu-interview',
        '/api/v1/offer/offer-list': 'menu-offer',
        '/api/v1/user/get-all-user': 'menu-user',
    };

    // Lấy id tương ứng từ map
    const activeMenuId = menuMap[currentPath];

    // Thêm class active nếu tìm thấy id
    if (activeMenuId) {
        const activeMenuItem = document.getElementById(activeMenuId);
        activeMenuItem?.classList.add('active');
    }
});
