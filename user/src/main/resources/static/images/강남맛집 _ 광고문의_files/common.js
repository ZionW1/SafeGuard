//
//스크롤바 너비 구하기
//
function getScrollbarWidth() {
    let scrollDiv = $("<div></div>")
        .css({
            width: "100px",
            height: "100px",
            overflow: "scroll",
            position: "absolute",
            top: "-9999px",
        })
        .appendTo("body");

    let scrollbarWidth = scrollDiv.get(0).offsetWidth - scrollDiv.get(0).clientWidth;

    scrollDiv.remove();

    return scrollbarWidth;
}

//
//팝업 뜨면 스크롤 막기
//
function scrollDisable() {
    let scrollbarWidth = getScrollbarWidth();
    // console.log("Scrollbar width:", scrollbarWidth);
    $("body").css("padding-right", `${scrollbarWidth}px`);
    $("header").css("width", `calc(100% - ${scrollbarWidth}px)`);

    $("body, html")
        .addClass("scroll_lock")
        .on("scroll touchmove mousewheel", function (e) {
            e.preventDefault();
        });
}
function scrollAble() {
    $("body").css("padding-right", 0);
    $("header").css("width", `100%`);
    $("body, html").removeClass("scroll_lock").off("scroll touchmove mousewheel");
}

//
// 페이지네이션
//
$(document).ready(function () {
    $(document).on("click", ".pagination_btn.num", function () {
        $(".pagination_btn").stop().removeClass("on");
        $(this).stop().addClass("on");
    });
});

//
//gsap scrollTrigger type1
//
function type1_createScrollTriggerAnimation(trigger, start, end, className, marker) {
    ScrollTrigger.create({
        trigger: trigger,
        start: start,
        end: end,
        markers: marker,
        onEnter: function () {
            $(trigger).stop().addClass(className);
        },
        onLeave: function () {
            $(trigger).stop().removeClass(className);
        },
        onEnterBack: function () {
            $(trigger).stop().addClass(className);
        },
        onLeaveBack: function () {
            $(trigger).stop().removeClass(className);
        },
    });
}

//
//gsap scrollTrigger type2
//
function type2_createScrollTriggerAnimation(trigger, start, end, className, marker) {
    ScrollTrigger.create({
        trigger: trigger,
        start: start,
        end: end,
        markers: marker,
        onEnter: function () {
            $(trigger).stop().addClass(className);
        },
    });
}
