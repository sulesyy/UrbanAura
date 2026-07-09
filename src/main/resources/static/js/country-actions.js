(function () {
    function normalize(value) {
        return (value || "").toLocaleLowerCase("tr-TR").trim();
    }

    const grid = document.querySelector("[data-country-grid]");
    const search = document.querySelector("[data-country-search]");
    const region = document.querySelector("[data-country-region]");
    const style = document.querySelector("[data-country-style]");
    const reset = document.querySelector("[data-country-reset]");
    const summary = document.querySelector("[data-filter-summary]");
    const compareTray = document.querySelector("[data-compare-tray]");
    const compareGrid = document.querySelector("[data-compare-grid]");
    const compareCount = document.querySelector("[data-compare-count]");
    const compareStart = document.querySelector("[data-compare-start]");
    const compareRun = document.querySelector("[data-compare-run]");
    const compareClear = document.querySelector("[data-compare-clear]");
    const compareResult = document.querySelector("[data-compare-result]");
    const compareItems = new Map();
    let compareMode = false;

    const queryAliases = {
        kultur: "kültür",
        culture: "kültür",
        doga: "doğa",
        nature: "doğa",
        luxury: "lüks",
        lux: "lüks"
    };

    function queryValue(value) {
        const normalized = normalize(value);
        return queryAliases[normalized] || normalized;
    }

    function setSelectValue(select, value) {
        if (!select || !value) {
            return;
        }
        const requestedValue = queryValue(value);
        Array.from(select.options).some(function (option) {
            if (normalize(option.value || option.textContent) === requestedValue) {
                select.value = option.value;
                return true;
            }
            return false;
        });
    }

    function filterCountries() {
        if (!grid) {
            return;
        }
        const query = normalize(search && search.value);
        const selectedRegion = normalize(region && region.value);
        const selectedStyle = normalize(style && style.value);
        let visibleCount = 0;

        grid.querySelectorAll(".country-card").forEach(function (card) {
            const isVisible = (!query || normalize(card.dataset.name).includes(query))
                && (!selectedRegion || normalize(card.dataset.region).includes(selectedRegion))
                && (!selectedStyle || normalize(card.dataset.style).includes(selectedStyle));
            card.hidden = !isVisible;
            visibleCount += isVisible ? 1 : 0;
        });

        grid.classList.toggle("has-no-results", visibleCount === 0);
        if (summary) {
            const parts = [];
            if (selectedStyle) {
                parts.push(style.options[style.selectedIndex].textContent);
            }
            if (selectedRegion) {
                parts.push(region.options[region.selectedIndex].textContent);
            }
            if (query) {
                parts.push("\"" + search.value.trim() + "\"");
            }
            summary.textContent = parts.length
                ? parts.join(" / ") + " için " + visibleCount + " ülke bulundu"
                : visibleCount + " ülke gösteriliyor";
        }
    }

    if (search) {
        search.addEventListener("input", filterCountries);
    }
    if (region) {
        region.addEventListener("change", filterCountries);
    }
    if (style) {
        style.addEventListener("change", filterCountries);
    }
    if (reset) {
        reset.addEventListener("click", function () {
            search.value = "";
            region.selectedIndex = 0;
            style.selectedIndex = 0;
            filterCountries();
        });
    }

    if (grid) {
        const params = new URLSearchParams(window.location.search);
        if (search && params.has("q")) {
            search.value = params.get("q") || "";
        }
        setSelectValue(region, params.get("region"));
        setSelectValue(style, params.get("style"));
        enhanceCompareCards();
        filterCountries();
    }

    function enhanceCompareCards() {
        if (!grid || !compareTray) {
            return;
        }
        grid.querySelectorAll(".country-card").forEach(function (card) {
            if (card.querySelector("[data-compare-toggle]")) {
                return;
            }
            const button = document.createElement("button");
            button.type = "button";
            button.className = "compare-toggle";
            button.dataset.compareToggle = card.getAttribute("href") || "";
            button.textContent = "+";
            button.setAttribute("aria-label", "Karşılaştırmaya ekle");
            button.setAttribute("aria-pressed", "false");
            card.appendChild(button);
        });
    }

    function cardImageClass(card) {
        return Array.from(card.classList).filter(function (className) {
            return className !== "country-card";
        }).join(" ");
    }

    function metricValue(card, metric) {
        const styleText = normalize(card.dataset.style);
        const regionText = normalize(card.dataset.region);
        if (metric === "Lüks") {
            return styleText.includes("lüks") ? 94 : 74;
        }
        if (metric === "Kültür") {
            return styleText.includes("kültür") ? 95 : regionText.includes("avrupa") ? 84 : 76;
        }
        if (metric === "Doğa") {
            return styleText.includes("doğa") ? 94 : 72;
        }
        return styleText.includes("modern") ? 93 : 78;
    }

    function renderCompareTray() {
        if (!compareTray || !compareGrid || !compareCount) {
            return;
        }
        const items = Array.from(compareItems.values());
        compareTray.hidden = !compareMode;
        compareCount.textContent = items.length + " ülke seçildi";
        compareGrid.innerHTML = items.map(function (item) {
            const metrics = ["Lüks", "Kültür", "Doğa", "Modernlik"].map(function (label) {
                const value = metricValue(item.card, label);
                return '<li><span>' + label + '</span><strong>' + value + '</strong></li>';
            }).join("");
            return '<article class="compare-card ' + item.imageClass + '">' +
                '<button type="button" data-compare-remove="' + item.href + '">×</button>' +
                '<div><span>' + item.region + '</span><h3>' + item.name + '</h3><p>' + item.style + '</p><ul>' + metrics + '</ul></div>' +
                '</article>';
        }).join("");
        if (!items.length) {
            compareGrid.innerHTML = '<div class="compare-empty">Karşılaştırmak için 2 veya 3 ülke seç.</div>';
        }
        if (compareResult && items.length < 2) {
            compareResult.hidden = true;
        }
    }

    function enterCompareMode() {
        compareMode = true;
        document.body.classList.add("is-compare-mode");
        if (compareStart) {
            compareStart.textContent = "Seçim Modu Açık";
        }
        renderCompareTray();
        if (compareTray) {
            compareTray.scrollIntoView({ behavior: "smooth", block: "nearest" });
        }
    }

    function exitCompareMode() {
        compareMode = false;
        compareItems.clear();
        document.body.classList.remove("is-compare-mode");
        if (compareStart) {
            compareStart.textContent = "Ülkeleri Karşılaştır";
        }
        document.querySelectorAll("[data-compare-toggle]").forEach(function (toggle) {
            toggle.classList.remove("is-selected");
            toggle.setAttribute("aria-pressed", "false");
            toggle.textContent = "+";
        });
        if (compareResult) {
            compareResult.hidden = true;
        }
        renderCompareTray();
    }

    function toggleCompare(card, toggle) {
        const href = card.getAttribute("href");
        if (!href) {
            return;
        }
        if (compareItems.has(href)) {
            compareItems.delete(href);
            toggle.classList.remove("is-selected");
            toggle.setAttribute("aria-pressed", "false");
            toggle.textContent = "+";
        } else {
            if (compareItems.size >= 3) {
                const firstKey = compareItems.keys().next().value;
                compareItems.delete(firstKey);
                const firstButton = grid.querySelector('[data-compare-toggle="' + firstKey + '"]');
                if (firstButton) {
                    firstButton.classList.remove("is-selected");
                    firstButton.setAttribute("aria-pressed", "false");
                    firstButton.textContent = "+";
                }
            }
            compareItems.set(href, {
                card: card,
                href: href,
                imageClass: cardImageClass(card),
                name: card.querySelector("h2").textContent.trim(),
                style: card.dataset.style || "",
                region: card.dataset.region || ""
            });
            toggle.classList.add("is-selected");
            toggle.setAttribute("aria-pressed", "true");
            toggle.textContent = "✓";
        }
        if (compareResult) {
            compareResult.hidden = true;
        }
        renderCompareTray();
    }

    function compareScore(item) {
        return ["Lüks", "Kültür", "Doğa", "Modernlik"].reduce(function (total, label) {
            return total + metricValue(item.card, label);
        }, 0);
    }

    function showCompareResult() {
        if (!compareResult) {
            return;
        }
        const items = Array.from(compareItems.values());
        if (items.length < 2) {
            compareResult.hidden = false;
            compareResult.innerHTML = '<strong>En az 2 ülke seçmelisin.</strong><p>Karşılaştırma sonucu için iki veya üç ülke seç.</p>';
            return;
        }
        const winner = items.slice().sort(function (first, second) {
            return compareScore(second) - compareScore(first);
        })[0];
        const bestMetric = ["Lüks", "Kültür", "Doğa", "Modernlik"].sort(function (first, second) {
            return metricValue(winner.card, second) - metricValue(winner.card, first);
        })[0];
        compareResult.hidden = false;
        compareResult.innerHTML = '<span>SONUÇ</span><strong>' + winner.name + ' daha güçlü görünüyor.</strong>' +
            '<p>Seçtiğin ülkeler içinde ' + bestMetric.toLowerCase() + ' puanı ve genel UrbanAura uyumu en yüksek seçenek bu rota.</p>' +
            '<a href="' + winner.href + '">Detay sayfasına git</a>';
    }

    document.addEventListener("click", function (event) {
        const compareToggle = event.target.closest("[data-compare-toggle]");
        if (compareToggle) {
            event.preventDefault();
            event.stopPropagation();
            if (!compareMode) {
                enterCompareMode();
            }
            toggleCompare(compareToggle.closest(".country-card"), compareToggle);
            return;
        }

        const compareRemove = event.target.closest("[data-compare-remove]");
        if (compareRemove) {
            event.preventDefault();
            const href = compareRemove.dataset.compareRemove;
            compareItems.delete(href);
            const toggle = grid && grid.querySelector('[data-compare-toggle="' + href + '"]');
            if (toggle) {
                toggle.classList.remove("is-selected");
                toggle.setAttribute("aria-pressed", "false");
                toggle.textContent = "+";
            }
            if (compareResult) {
                compareResult.hidden = true;
            }
            renderCompareTray();
            return;
        }

        const toggle = event.target.closest("[data-favorite-toggle]");
        if (!toggle) {
            return;
        }
        event.preventDefault();
        event.stopPropagation();

        fetch("/api/favorites/" + toggle.dataset.favoriteToggle, { method: "POST" }).then(function (response) {
            if (response.status === 401) {
                window.location.href = "/login";
                return null;
            }
            return response.ok ? response.json() : Promise.reject();
        }).then(function (result) {
            if (!result) {
                return;
            }
            toggle.classList.toggle("is-favorite", result.favorite);
            toggle.setAttribute("aria-pressed", String(result.favorite));
            const icon = toggle.querySelector("[data-favorite-icon]");
            const label = toggle.querySelector("[data-favorite-label]");
            if (icon) {
                icon.textContent = result.favorite ? "♥" : "♡";
            } else {
                toggle.textContent = result.favorite ? "♥" : "♡";
            }
            if (label) {
                label.textContent = result.favorite ? "Favorilerde" : "Favorilere Ekle";
            }
            if (toggle.closest(".favorite-grid") && !result.favorite) {
                toggle.closest(".country-card").remove();
                const count = document.querySelector("[data-favorite-count]");
                if (count) {
                    count.textContent = String(result.count);
                }
                const remaining = document.querySelectorAll(".favorite-grid .country-card").length;
                const empty = document.querySelector("[data-favorites-empty]");
                if (empty) {
                    empty.hidden = remaining !== 0;
                }
                updateFavoriteCollections();
            }
        }).catch(function () {
            toggle.classList.add("has-error");
            window.setTimeout(function () {
                toggle.classList.remove("has-error");
            }, 850);
        });
    });

    if (compareClear) {
        compareClear.addEventListener("click", function () {
            exitCompareMode();
        });
    }

    if (compareStart) {
        compareStart.addEventListener("click", function () {
            if (compareMode) {
                exitCompareMode();
            } else {
                enterCompareMode();
            }
        });
    }

    if (compareRun) {
        compareRun.addEventListener("click", showCompareResult);
    }

    const favoriteGrid = document.querySelector("[data-favorite-grid]");
    const favoriteFilterButtons = document.querySelectorAll("[data-favorite-filter]");
    const favoriteFilterEmpty = document.querySelector("[data-favorites-filter-empty]");
    let activeFavoriteFilter = "all";

    function favoriteCards() {
        return favoriteGrid ? Array.from(favoriteGrid.querySelectorAll("[data-favorite-card]")) : [];
    }

    function updateFavoriteCollections() {
        if (!favoriteGrid) {
            return;
        }
        const counts = { all: 0, dream: 0, holiday: 0, culture: 0, luxury: 0 };
        favoriteCards().forEach(function (card) {
            const collection = card.dataset.favoriteCardCollection || "dream";
            counts.all += 1;
            counts[collection] = (counts[collection] || 0) + 1;
        });

        Object.keys(counts).forEach(function (key) {
            const count = document.querySelector('[data-favorite-filter-count="' + key + '"]');
            if (count) {
                count.textContent = String(counts[key]);
            }
        });

        let visibleCount = 0;
        favoriteCards().forEach(function (card) {
            const isVisible = activeFavoriteFilter === "all"
                || (card.dataset.favoriteCardCollection || "dream") === activeFavoriteFilter;
            card.hidden = !isVisible;
            visibleCount += isVisible ? 1 : 0;
        });

        if (favoriteFilterEmpty) {
            favoriteFilterEmpty.hidden = counts.all === 0 || visibleCount !== 0;
        }

        favoriteFilterButtons.forEach(function (button) {
            const isActive = button.dataset.favoriteFilter === activeFavoriteFilter;
            button.classList.toggle("is-active", isActive);
            button.setAttribute("aria-pressed", String(isActive));
        });
    }

    favoriteFilterButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            activeFavoriteFilter = button.dataset.favoriteFilter || "all";
            updateFavoriteCollections();
        });
    });

    function saveFavoriteMeta(slug) {
        const note = document.querySelector('[data-favorite-note="' + slug + '"]');
        const collection = document.querySelector('[data-favorite-collection="' + slug + '"]');
        if (!note || !collection) {
            return;
        }
        const body = new URLSearchParams({
            note: note.value,
            collection: collection.value
        });
        fetch("/api/favorite-meta/" + slug, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: body.toString()
        }).then(function (response) {
            if (response.status === 401) {
                window.location.href = "/login";
                return;
            }
            if (!response.ok) {
                throw new Error("Meta save failed");
            }
            note.classList.remove("has-error");
            collection.classList.remove("has-error");
            note.classList.add("is-saved");
            collection.classList.add("is-saved");
            const card = collection.closest("[data-favorite-card]");
            if (card) {
                card.dataset.favoriteCardCollection = collection.value || "dream";
            }
            updateFavoriteCollections();
            window.setTimeout(function () {
                note.classList.remove("is-saved");
                collection.classList.remove("is-saved");
            }, 650);
        }).catch(function () {
            note.classList.add("has-error");
            collection.classList.add("has-error");
        });
    }

    const metaTimers = new Map();
    document.querySelectorAll("[data-favorite-note]").forEach(function (field) {
        field.addEventListener("input", function () {
            const slug = field.dataset.favoriteNote;
            window.clearTimeout(metaTimers.get(slug));
            metaTimers.set(slug, window.setTimeout(function () {
                saveFavoriteMeta(slug);
            }, 420));
        });
    });

    document.querySelectorAll("[data-favorite-collection]").forEach(function (field) {
        field.addEventListener("change", function () {
            saveFavoriteMeta(field.dataset.favoriteCollection);
        });
    });

    updateFavoriteCollections();
}());
