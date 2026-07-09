(function () {
    const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    const navbar = document.querySelector(".navbar");
    const navigation = navbar && navbar.querySelector(".nav-links");

    if (navbar && navigation) {
        const toggle = document.createElement("button");
        toggle.type = "button";
        toggle.className = "nav-toggle";
        toggle.setAttribute("aria-label", "Menüyü aç");
        toggle.setAttribute("aria-expanded", "false");
        toggle.innerHTML = "<span></span><span></span><span></span>";
        navbar.classList.add("has-mobile-nav");
        navbar.insertBefore(toggle, navbar.querySelector(".nav-actions"));

        toggle.addEventListener("click", function () {
            const isOpen = navbar.classList.toggle("nav-open");
            toggle.setAttribute("aria-expanded", String(isOpen));
            toggle.setAttribute("aria-label", isOpen ? "Menüyü kapat" : "Menüyü aç");
        });

        navigation.addEventListener("click", function (event) {
            if (event.target.closest("a")) {
                navbar.classList.remove("nav-open");
                toggle.setAttribute("aria-expanded", "false");
            }
        });
    }

    document.body.classList.add("page-mounted");
    const revealElements = document.querySelectorAll(
        ".hero-content > *, .home-personal-panel, .home-next-grid > *, .feature-card, .countries-body .page-heading, .countries-body .filter-bar, .filter-summary, .countries-body .country-card, .quiz-shell, .favorites-main .page-heading, .favorite-grid .country-card, .profile-panel, .detail-hero, .info-strip, .detail-insight-grid > *, .detail-tabs-section, .related-country-grid > *, .guide-grid > *, .mini-gallery > *, .discover-hero, .recommendation-card, .explore-card, .spain-food-card"
    );

    if (!reduceMotion && revealElements.length) {
        revealElements.forEach(function (element, index) {
            element.classList.add("reveal-ready");
            element.style.setProperty("--reveal-delay", Math.min(index * 38, 228) + "ms");
        });

        const observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add("is-visible");
                    observer.unobserve(entry.target);
                }
            });
        }, {
            threshold: 0.12,
            rootMargin: "0px 0px -24px 0px"
        });

        revealElements.forEach(function (element) {
            observer.observe(element);
        });
    }

    const detailTabs = document.querySelector("[data-detail-tabs]");
    if (detailTabs) {
        detailTabs.addEventListener("click", function (event) {
            const tab = event.target.closest("[data-detail-tab]");
            if (!tab) {
                return;
            }
            const target = tab.dataset.detailTab;
            detailTabs.querySelectorAll("[data-detail-tab]").forEach(function (button) {
                button.classList.toggle("active", button === tab);
            });
            detailTabs.querySelectorAll("[data-detail-panel]").forEach(function (panel) {
                panel.classList.toggle("active", panel.dataset.detailPanel === target);
            });
        });
    }

    const hero = document.querySelector(".home-page .hero");
    if (!hero || reduceMotion || window.matchMedia("(pointer: coarse)").matches) {
        return;
    }

    const light = document.createElement("span");
    light.className = "hero-pointer-light";
    light.setAttribute("aria-hidden", "true");
    hero.appendChild(light);

    hero.addEventListener("pointermove", function (event) {
        const bounds = hero.getBoundingClientRect();
        hero.style.setProperty("--light-x", ((event.clientX - bounds.left) / bounds.width * 100) + "%");
        hero.style.setProperty("--light-y", ((event.clientY - bounds.top) / bounds.height * 100) + "%");
        hero.classList.add("has-pointer-light");
    });

    hero.addEventListener("pointerleave", function () {
        hero.classList.remove("has-pointer-light");
    });
}());
