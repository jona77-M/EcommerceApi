//============================================
//Task 1: The Script Foundation & Data Structure
// create this script.js file
//============================================
// ===============================
// ===============================fixing

/*
Task 1 logic: 
- This section builds the foundation of the whole script by defining the main classes:
  Product, CartItem, Order, and User.
- It also prepares shared constants and localStorage helpers so cart data, order history,
  and the latest placed order can be saved and reused across different pages.
- The pricing helper functions are placed here too, because subtotal, shipping, discount,
  and total are needed in both cart and checkout behavior.
*/

class Product {
    constructor(id, name, price, image, detailPage) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.detailPage = detailPage;
    }
}

class CartItem {
    constructor(product, quantity) {
        this.id = product.id;
        this.name = product.name;
        this.price = product.price;
        this.image = product.image;
        this.detailPage = product.detailPage;
        this.quantity = quantity;
    }
}

class Order {
    constructor(id, date, subtotal, shipping, discount, total, items, status) {
        this.id = id;
        this.date = date;
        this.subtotal = subtotal;
        this.shipping = shipping;
        this.discount = discount;
        this.total = total;
        this.items = items;
        this.status = status;
    }
}

class User {
    constructor(name, orderHistory) {
        this.name = name;
        this.orderHistory = orderHistory;
    }
}

let products = [];

const CART_STORAGE_KEY = "lab6_cart";
const ORDER_HISTORY_STORAGE_KEY = "lab6_order_history";
const LAST_ORDER_STORAGE_KEY = "lab6_last_order";
const SHIPPING_FEE_PER_ITEM = 4;
const DISCOUNT_RATE = 0.10;
const PRODUCTS_API_URL = "http://localhost:8080/api/v1/products";

function getDefaultOrderHistory() {
    return [
        new Order(
            "0928586",
            "February 15, 2024",
            49.99,
            4,
            5,
            48.99,
            [
                { name: "4Tech Mouse", quantity: 1, price: 49.99, subtotal: 49.99 }
            ],
            "Delivered"
        ),
        new Order(
            "1032471",
            "March 3, 2024",
            15000,
            4,
            1500,
            13504,
            [
                { name: "Victus Laptop", quantity: 1, price: 15000, subtotal: 15000 }
            ],
            "Shipped"
        )
    ];
}

function normalizeOrder(order) {
    return {
        id: order.id || String(Date.now()),
        date: order.date || new Date().toLocaleDateString(),
        subtotal: Number(order.subtotal || 0),
        shipping: Number(order.shipping || 0),
        discount: Number(order.discount || 0),
        total: Number(order.total || 0),
        status: order.status || "Processing",
        items: Array.isArray(order.items) ? order.items : []
    };
}

function loadOrderHistory() {
    const savedOrders = localStorage.getItem(ORDER_HISTORY_STORAGE_KEY);

    if (!savedOrders) {
        const defaultOrders = getDefaultOrderHistory().map(normalizeOrder);
        localStorage.setItem(ORDER_HISTORY_STORAGE_KEY, JSON.stringify(defaultOrders));
        return defaultOrders;
    }

    try {
        return JSON.parse(savedOrders).map(normalizeOrder);
    } catch (error) {
        const fallbackOrders = getDefaultOrderHistory().map(normalizeOrder);
        localStorage.setItem(ORDER_HISTORY_STORAGE_KEY, JSON.stringify(fallbackOrders));
        return fallbackOrders;
    }
}

function saveOrderHistory(orderHistory) {
    localStorage.setItem(ORDER_HISTORY_STORAGE_KEY, JSON.stringify(orderHistory));
}

function saveLastOrder(order) {
    localStorage.setItem(LAST_ORDER_STORAGE_KEY, JSON.stringify(order));
}

function loadLastOrder() {
    const savedOrder = localStorage.getItem(LAST_ORDER_STORAGE_KEY);

    if (!savedOrder) {
        return null;
    }

    try {
        return normalizeOrder(JSON.parse(savedOrder));
    } catch (error) {
        return null;
    }
}

const currentUser = new User("Jona", loadOrderHistory());

function saveCart() {
    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart));
}

function calculateCartSummary(cartItems) {
    const subtotal = cartItems.reduce(function(sum, item) {
        return sum + (item.price * item.quantity);
    }, 0);
    const totalQuantity = cartItems.reduce(function(sum, item) {
        return sum + item.quantity;
    }, 0);
    const shipping = totalQuantity * SHIPPING_FEE_PER_ITEM;
    const discount = subtotal * DISCOUNT_RATE;
    const total = subtotal + shipping - discount;

    return {
        subtotal: subtotal,
        shipping: shipping,
        discount: discount,
        total: total,
        totalQuantity: totalQuantity
    };
}

function getCartItemCount() {
    return cart.reduce(function(sum, item) {
        return sum + item.quantity;
    }, 0);
}

function updateCartLinks() {
    const cartLinks = document.querySelectorAll('a[href="cart.html"]');
    const cartItemCount = getCartItemCount();

    cartLinks.forEach(function(link) {
        link.textContent = "Cart (" + cartItemCount + ")";
    });
}

function animateAddToCart(productCard) {
    const cartLink = document.querySelector('a[href="cart.html"]');

    if (!productCard || !cartLink) {
        return;
    }

    const productRect = productCard.getBoundingClientRect();
    const cartRect = cartLink.getBoundingClientRect();
    const flyingCard = productCard.cloneNode(true);

    flyingCard.classList.remove("fade-in");
    flyingCard.classList.add("cart-fly-item");
    flyingCard.style.top = productRect.top + "px";
    flyingCard.style.left = productRect.left + "px";
    flyingCard.style.width = productRect.width + "px";
    flyingCard.style.height = productRect.height + "px";

    document.body.appendChild(flyingCard);

    requestAnimationFrame(function() {
        flyingCard.style.top = cartRect.top + "px";
        flyingCard.style.left = cartRect.left + "px";
        flyingCard.style.width = "60px";
        flyingCard.style.height = "60px";
        flyingCard.style.opacity = "0.2";
        flyingCard.style.transform = "scale(0.25)";
    });

    setTimeout(function() {
        flyingCard.remove();
        cartLink.classList.add("cart-link-pop");

        setTimeout(function() {
            cartLink.classList.remove("cart-link-pop");
        }, 300);
    }, 700);
}

function loadCart() {
    const savedCart = localStorage.getItem(CART_STORAGE_KEY);

    if (!savedCart) {
        return [];
    }

    const parsedCart = JSON.parse(savedCart);

    return parsedCart.map(function(item) {
        const product = new Product(
            item.id,
            item.name,
            item.price,
            item.image,
            item.detailPage || "detail.html"
        );

        return new CartItem(product, item.quantity);
    });
}

function formatPrice(value) {
    return "$" + Number(value).toLocaleString(undefined, {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function getProductImage(productData) {
    if (productData.imageUrl) {
        return productData.imageUrl;
    }

    if (productData.image) {
        return productData.image;
    }

    const normalizedName = String(
        productData.productName || productData.name || ""
    ).toLowerCase();

    if (normalizedName.includes("mouse")) {
        return "assets/mouse.jpg";
    }

    if (normalizedName.includes("tablet")) {
        return "assets/tablet.jpg";
    }

    if (normalizedName.includes("laptop") || normalizedName.includes("victus")) {
        return "assets/victus.jpg";
    }

    if (normalizedName.includes("charger")) {
        return "assets/charger1.jpg";
    }

    if (normalizedName.includes("nintendo")) {
        return "assets/nintendo.jpg";
    }

    if (normalizedName.includes("airpod")) {
        return "assets/airpod.jpg";
    }

    if (normalizedName.includes("iphone")) {
        return "assets/iphone.jpg";
    }

    if (normalizedName.includes("powerbank")) {
        return "assets/powerbank.jpg";
    }

    if (normalizedName.includes("watch")) {
        return "assets/smart_watch.jpg";
    }

    return "assets/logo.png";
}

function mapApiProductToProduct(productData) {
    return new Product(
        productData.id,
        productData.productName,
        Number(productData.price),
        getProductImage(productData),
        "detail.html"
    );
}



async function fetchProducts() {
    try {
        const response = await fetch(PRODUCTS_API_URL);

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error("Products endpoint not found (404).");
            }

            if (response.status === 500) {
                throw new Error("Server error while fetching products (500).");
            }

            throw new Error("Failed to fetch products. Status: " + response.status);
        }

        const data = await response.json();

        if (!Array.isArray(data)) {
            console.error("fetchProducts error: API response is not an array.");
            return [];
        }

        return data.map(mapApiProductToProduct);
    } catch (error) {
        console.error("fetchProducts error:", error.message);
        return [];
    }
}

function renderLandingProducts() {
    const featuredContainer = document.querySelector(".section2");
    const discountedContainer = document.querySelector(".section3");

    if (!featuredContainer && !discountedContainer) {
        return;
    }

    if (featuredContainer) {
        featuredContainer.textContent = "";
    }

    if (discountedContainer) {
        discountedContainer.textContent = "";
    }

    if (products.length === 0) {
        if (featuredContainer) {
            featuredContainer.innerHTML = "<p>No products available right now.</p>";
        }

        if (discountedContainer) {
            discountedContainer.innerHTML = "<p>No discounted products available right now.</p>";
        }

        return;
    }

    if (featuredContainer) {
        products.slice(0, 5).forEach(function(product) {
            const card = document.createElement("figure");
            const image = document.createElement("img");
            const title = document.createElement("h3");
            const price = document.createElement("p");

            image.setAttribute("src", product.image);
            image.setAttribute("alt", product.name);
            title.appendChild(document.createTextNode(product.name));
            price.appendChild(document.createTextNode("Price: " + formatPrice(product.price)));

            card.appendChild(image);
            card.appendChild(title);
            card.appendChild(price);

            card.addEventListener("click", function() {
                window.location.href = product.detailPage;
            });

            featuredContainer.appendChild(card);
        });
    }

    if (discountedContainer) {
        products.slice(5, 10).forEach(function(product) {
            const card = document.createElement("figure");
            const image = document.createElement("img");
            const title = document.createElement("h3");
            const salePrice = document.createElement("p");
            const originalPrice = document.createElement("del");
            const discountedValue = Math.round(product.price * 0.85);

            image.setAttribute("src", product.image);
            image.setAttribute("alt", product.name);
            title.appendChild(document.createTextNode(product.name));
            salePrice.appendChild(document.createTextNode("Price: " + formatPrice(discountedValue)));
            originalPrice.appendChild(document.createTextNode(formatPrice(product.price)));

            card.appendChild(image);
            card.appendChild(title);
            card.appendChild(salePrice);
            card.appendChild(originalPrice);

            card.addEventListener("click", function() {
                window.location.href = product.detailPage;
            });

            discountedContainer.appendChild(card);
        });
    }
}

function initializeLandingHeroSlider() {
    const heroSection = document.querySelector(".section1");

    if (!heroSection) {
        return;
    }

    const heroTitle = heroSection.querySelector("h1");
    const heroText = heroSection.querySelector("p");
    const heroButton = heroSection.querySelector("button");
    const heroSlides = products.slice(0, 4);
    let currentSlideIndex = 0;

    if (heroSlides.length === 0) {
        heroTitle.textContent = "Academic Tech Shop";
        heroText.textContent = "No featured products available right now.";
        heroButton.textContent = "Browse Later";
        heroButton.disabled = true;
        return;
    }

    function renderHeroSlide() {
        const activeProduct = heroSlides[currentSlideIndex];

        heroSection.style.backgroundImage =
            "linear-gradient(135deg, rgba(8, 15, 32, 0.84), rgba(0, 123, 255, 0.42)), url('" +
            activeProduct.image +
            "')";

        heroTitle.textContent = activeProduct.name;
        heroText.textContent =
            "Discover premium gadgets for study, work, and play. Shop the " +
            activeProduct.name +
            " and explore more top tech in our collection.";
        heroButton.textContent = "Shop Now";
        heroButton.disabled = false;
        heroButton.onclick = function() {
            window.location.href = activeProduct.detailPage;
        };

        heroSection.classList.remove("hero-slide-active");

        requestAnimationFrame(function() {
            heroSection.classList.add("hero-slide-active");
        });
    }

    renderHeroSlide();

    setInterval(function() {
        currentSlideIndex = (currentSlideIndex + 1) % heroSlides.length;
        renderHeroSlide();
    }, 3000);
}

// ===============================
// Task 2: Dynamic Product Rendering
// ===============================

/*
Task 2 logic:
- This section creates the product cards dynamically from the JavaScript products array
  instead of manually writing every product in HTML.
- Each card shows the product image, name, price, and Add to Cart button.
- The card is also interactive, so clicking the product area or pressing Enter sends the
  user to the related detail page.
*/

function renderProductGrid() {
    const productContainer = document.querySelector(".product-grid");

    if (!productContainer) {
        return;
    }

    productContainer.textContent = "";

    if (products.length === 0) {
        productContainer.innerHTML = "<p>No products available right now.</p>";
        return;
    }

    products.forEach(function(product) {
        const productCard = document.createElement("article");
        productCard.classList.add("product-card");
        productCard.setAttribute("data-href", product.detailPage);
        productCard.setAttribute("tabindex", "0");
        productCard.setAttribute("role", "link");

        const productImage = document.createElement("img");
        productImage.setAttribute("src", product.image);
        productImage.setAttribute("alt", product.name);

        const productBody = document.createElement("div");
        productBody.classList.add("product-body");

        const productTitle = document.createElement("h3");
        productTitle.appendChild(document.createTextNode(product.name));

        const productPrice = document.createElement("p");
        productPrice.classList.add("price");
        productPrice.appendChild(document.createTextNode(formatPrice(product.price)));

        const actionContainer = document.createElement("div");
        actionContainer.classList.add("actions");

        const addToCartButton = document.createElement("button");
        addToCartButton.setAttribute("type", "button");
        addToCartButton.setAttribute("data-id", product.id);
        addToCartButton.appendChild(document.createTextNode("Add to Cart"));

        actionContainer.appendChild(addToCartButton);
        productBody.appendChild(productTitle);
        productBody.appendChild(productPrice);
        productBody.appendChild(actionContainer);
        productCard.appendChild(productImage);
        productCard.appendChild(productBody);

        productCard.addEventListener("click", function(event) {
            if (event.target.tagName === "BUTTON") {
                return;
            }

            window.location.href = productCard.getAttribute("data-href");
        });

        productCard.addEventListener("keydown", function(event) {
            if (event.key === "Enter") {
                window.location.href = productCard.getAttribute("data-href");
            }
        });

        productContainer.appendChild(productCard);
    });
}

// =======================
// Task 3: Event Handling & The Cart
// =======================

/*
Task 3 logic:
- This section controls cart interactions such as adding items, increasing quantity,
  decreasing quantity, removing items, and redrawing the cart display.
- It keeps the cart synchronized with localStorage so the cart remains available after
  refreshing the page or moving to another page.
- It also updates related user interface parts like the cart count in navigation,
  the cart total, and the checkout summary preview.
*/

let cart = loadCart();

document.body.addEventListener("click", function(event) {
    if (event.target.tagName === "BUTTON" && event.target.textContent === "Add to Cart") {
        const productId = event.target.getAttribute("data-id");

        const selectedProduct = products.find(function(product) {
            return product.id == productId;
        });

        if (!selectedProduct) {
            return;
        }

        const existingItem = cart.find(function(item) {
            return item.id == productId;
        });

        if (existingItem) {
            existingItem.quantity += 1;
        } else {
            cart.push(new CartItem(selectedProduct, 1));
        }

        saveCart();
        renderCart();
        updateCartLinks();

        const productCard = event.target.closest(".product-card");

        if (productCard) {
            productCard.classList.add("fade-in");
            animateAddToCart(productCard);

            setTimeout(function() {
                productCard.classList.remove("fade-in");
            }, 700);
        }
    }
});

function renderCart() {
    const cartList = document.querySelector(".cart-list");
    const totalDisplay = document.getElementById("cart-total");
    const cartEmptyContainer = document.querySelector(".cart-empty-container");
    const cartTotalContainer = document.querySelector(".cart-total-container");

    updateCartLinks();

    if (!cartList) {
        renderCheckoutSummary();
        return;
    }

    cartList.textContent = "";

    if (cart.length === 0) {
        if (totalDisplay) {
            totalDisplay.textContent = formatPrice(0);
        }

        if (cartEmptyContainer) {
            cartEmptyContainer.style.display = "block";
        }

        if (cartTotalContainer) {
            cartTotalContainer.style.display = "none";
        }

        renderCheckoutSummary();
        return;
    }

    if (cartEmptyContainer) {
        cartEmptyContainer.style.display = "none";
    }

    if (cartTotalContainer) {
        cartTotalContainer.style.display = "block";
    }

    cart.forEach(function(item) {
        const listItem = document.createElement("li");
        listItem.classList.add("cart-item");
        listItem.setAttribute("data-id", item.id);

        const itemInfo = document.createElement("div");
        itemInfo.classList.add("cart-item-info");

        const itemImage = document.createElement("img");
        itemImage.classList.add("image-content");
        itemImage.setAttribute("src", item.image);
        itemImage.setAttribute("alt", item.name);

        const itemText = document.createElement("div");
        itemText.classList.add("cart-item-text");

        const name = document.createElement("h3");
        name.appendChild(document.createTextNode(item.name));

        const price = document.createElement("p");
        price.appendChild(document.createTextNode(formatPrice(item.price)));

        const itemControls = document.createElement("div");
        itemControls.classList.add("cart-item-controls");

        const quantityControls = document.createElement("div");
        quantityControls.classList.add("quantity-controls");

        const decreaseButton = document.createElement("button");
        decreaseButton.setAttribute("type", "button");
        decreaseButton.setAttribute("data-action", "decrease");
        decreaseButton.setAttribute("data-id", item.id);
        decreaseButton.classList.add("quantity-button");
        decreaseButton.appendChild(document.createTextNode("-"));

        const quantityInput = document.createElement("input");
        quantityInput.setAttribute("type", "text");
        quantityInput.setAttribute("readonly", "readonly");
        quantityInput.setAttribute("data-id", item.id);
        quantityInput.classList.add("quantity-display");
        quantityInput.value = item.quantity;

        const increaseButton = document.createElement("button");
        increaseButton.setAttribute("type", "button");
        increaseButton.setAttribute("data-action", "increase");
        increaseButton.setAttribute("data-id", item.id);
        increaseButton.classList.add("quantity-button");
        increaseButton.appendChild(document.createTextNode("+"));

        const subtotal = document.createElement("p");
        subtotal.classList.add("cart-item-subtotal");
        subtotal.appendChild(document.createTextNode(formatPrice(item.price * item.quantity)));

        itemText.appendChild(name);
        itemText.appendChild(price);
        itemInfo.appendChild(itemImage);
        itemInfo.appendChild(itemText);

        quantityControls.appendChild(decreaseButton);
        quantityControls.appendChild(quantityInput);
        quantityControls.appendChild(increaseButton);

        itemControls.appendChild(quantityControls);
        itemControls.appendChild(subtotal);

        listItem.appendChild(itemInfo);
        listItem.appendChild(itemControls);

        cartList.appendChild(listItem);
    });

    const summary = calculateCartSummary(cart);

    if (totalDisplay) {
        totalDisplay.textContent = formatPrice(summary.subtotal);
    }

    renderCheckoutSummary();
}

function renderCheckoutSummary() {
    const summaryItemsList = document.getElementById("checkout-summary-items");
    const subtotalElement = document.getElementById("checkout-subtotal");
    const shippingElement = document.getElementById("checkout-shipping");
    const discountElement = document.getElementById("checkout-discount");
    const totalElement = document.getElementById("checkout-total");
    const summaryNote = document.getElementById("checkout-summary-note");
    const placeOrderButton = document.querySelector(".place-order");

    if (!subtotalElement || !shippingElement || !discountElement || !totalElement) {
        return;
    }

    const summary = calculateCartSummary(cart);

    if (summaryItemsList) {
        summaryItemsList.textContent = "";

        cart.forEach(function(item) {
            const listItem = document.createElement("li");
            listItem.classList.add("summary-item");

            const itemInfo = document.createElement("div");
            const itemName = document.createElement("p");
            const itemMeta = document.createElement("p");
            const itemTotal = document.createElement("p");
            const itemTotalStrong = document.createElement("strong");

            itemName.innerHTML = "<strong>" + item.name + "</strong>";
            itemMeta.textContent = "Price: " + formatPrice(item.price) + " | Qty: " + item.quantity;
            itemTotalStrong.textContent = formatPrice(item.price * item.quantity);
            itemTotal.appendChild(itemTotalStrong);

            itemInfo.appendChild(itemName);
            itemInfo.appendChild(itemMeta);
            listItem.appendChild(itemInfo);
            listItem.appendChild(itemTotal);
            summaryItemsList.appendChild(listItem);
        });
    }

    subtotalElement.textContent = formatPrice(summary.subtotal);
    shippingElement.textContent = formatPrice(summary.shipping);
    discountElement.textContent = "- " + formatPrice(summary.discount);
    totalElement.textContent = formatPrice(summary.total);

    if (summaryNote) {
        if (cart.length === 0) {
            summaryNote.textContent = "Your cart is empty. Add products before placing an order.";
        } else {
            summaryNote.textContent =
                "Shipping is " +
                formatPrice(SHIPPING_FEE_PER_ITEM) +
                " for each item quantity, with a 10% discount on subtotal.";
        }
    }

    if (placeOrderButton) {
        placeOrderButton.disabled = cart.length === 0;
    }
}

function removeCartItemWithAnimation(id) {
    const cartItemElement = document.querySelector('.cart-item[data-id="' + id + '"]');

    if (cartItemElement) {
        cartItemElement.classList.add("cart-item-removing");

        setTimeout(function() {
            cart = cart.filter(function(cartItem) {
                return cartItem.id != id;
            });

            saveCart();
            renderCart();
        }, 450);
    } else {
        cart = cart.filter(function(cartItem) {
            return cartItem.id != id;
        });

        saveCart();
        renderCart();
    }
}

document.body.addEventListener("click", function(event) {
    if (!event.target.hasAttribute("data-action")) {
        return;
    }

    const action = event.target.getAttribute("data-action");
    const id = event.target.getAttribute("data-id");
    const item = cart.find(function(cartItem) {
        return cartItem.id == id;
    });

    if (!item) {
        return;
    }

    if (action === "increase") {
        item.quantity += 1;
        saveCart();
        renderCart();
        return;
    }

    if (action === "decrease") {
        if (item.quantity === 1) {
            removeCartItemWithAnimation(id);
            return;
        }

        item.quantity -= 1;
        saveCart();
        renderCart();
    }
});

// =======================
// Task 4: Form Validation & Submission (checkout.html)
// =======================

/*
Task 4 logic:
- This section validates the checkout form before an order can be placed.
- It checks the shipping inputs first, then validates card details only when the selected
  payment method is credit card.
- If everything is valid and the cart is not empty, it creates an order object from the
  cart data, saves it into order history, stores the latest order ID for the thank-you
  page, clears the cart, and redirects the user to the confirmation page.
*/

const paymentForm = document.querySelector("#paymentForm");

if (paymentForm) {
    const fullNameInput = document.querySelector("#fullName");
    const streetInput = document.querySelector("#street");
    const zipInput = document.querySelector("#zip");
    const cardNumberInput = document.querySelector("#cardNumber");
    const expiryInput = document.querySelector("#expiry");
    const cvvInput = document.querySelector("#cvv");

    function getErrorElement(input) {
        let errorElement = input.parentElement.querySelector(".error-message");

        if (!errorElement) {
            errorElement = document.createElement("small");
            errorElement.classList.add("error-message");
            input.parentElement.appendChild(errorElement);
        }

        return errorElement;
    }

    function showError(input, message) {
        input.classList.add("error");
        getErrorElement(input).textContent = message;
    }

    function clearError(input) {
        input.classList.remove("error");
        getErrorElement(input).textContent = "";
    }

    paymentForm.addEventListener("submit", async function(event) {
        event.preventDefault();

        let isValid = true;
        const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked');

        clearError(fullNameInput);
        clearError(streetInput);
        clearError(zipInput);
        clearError(cardNumberInput);
        clearError(expiryInput);
        clearError(cvvInput);

        if (fullNameInput.value.trim() === "") {
            showError(fullNameInput, "Full name is required.");
            isValid = false;
        }

        if (streetInput.value.trim() === "") {
            showError(streetInput, "Street address is required.");
            isValid = false;
        }

        if (zipInput.value.trim() === "") {
            showError(zipInput, "Zip code is required.");
            isValid = false;
        }

        if (paymentMethod && paymentMethod.value === "card") {
            if (cardNumberInput.value.trim() === "") {
                showError(cardNumberInput, "Card number is required.");
                isValid = false;
            }

            if (expiryInput.value.trim() === "") {
                showError(expiryInput, "Expiry date is required.");
                isValid = false;
            }

            if (cvvInput.value.trim() === "") {
                showError(cvvInput, "CVV is required.");
                isValid = false;
            }
        }

        if (cart.length === 0) {
            isValid = false;

            const summaryNote = document.getElementById("checkout-summary-note");

            if (summaryNote) {
                summaryNote.textContent = "Your cart is empty. Add products before placing an order.";
            }
        }

        if (isValid) {
            const summary = calculateCartSummary(cart);
            const orderItems = cart.map(function(item) {
                return {
                    id: item.id,
                    name: item.name,
                    quantity: item.quantity,
                    price: item.price,
                    subtotal: item.price * item.quantity
                };
            });
            const newOrder = normalizeOrder({
                id: "ATS-" + Date.now(),
                date: new Date().toLocaleString(undefined, {
                    year: "numeric",
                    month: "long",
                    day: "numeric",
                    hour: "numeric",
                    minute: "2-digit"
                }),
                subtotal: summary.subtotal,
                shipping: summary.shipping,
                discount: summary.discount,
                total: summary.total,
                items: orderItems,
                status: "Processing",
                shippingAddress: {
                    fullName: fullNameInput.value.trim(),
                    street: streetInput.value.trim(),
                    zip: zipInput.value.trim()
                },
                paymentMethod: paymentMethod ? paymentMethod.value : "card"
            });

            const orderResponse = await fetch("http://localhost:8080/api/v1/orders", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    customerName: fullNameInput.value.trim(),
                    items: cart.map(function(item) {
                        return {
                            productId: item.id,
                            quantity: item.quantity
                        };
                    })
                })
            });

            if (!orderResponse.ok) {
                alert("Order was not saved. Please try again.");
                return;
            }

            const updatedOrderHistory = [newOrder].concat(loadOrderHistory());

            saveOrderHistory(updatedOrderHistory);
            saveLastOrder(newOrder);
            currentUser.orderHistory = updatedOrderHistory;
            cart = [];
            saveCart();
            renderCart();
            window.location.href = "thankyou.html";
        }
    });
}

// =======================
// Task 5: User Account & Order History (account.html)
// =======================

/*
Task 5 logic:
- This section updates the account page with the current user's name and loads the saved
  orders from localStorage.
- It renders each order into the Order History section and shows extra details such as
  date, status, subtotal, shipping, discount, total, and purchased items.
- It also updates the thank-you page by showing the most recent saved order ID after a
  successful checkout.
*/

const accountGreeting = document.querySelector("#accountGreeting");
const accountName = document.querySelector("#accountName");
const orderHistoryList = document.querySelector("#order-history-list");

if (accountGreeting && accountName) {
    accountGreeting.textContent = currentUser.name + "'s Account";
    accountName.textContent = currentUser.name;
}

function renderOrderHistory() {
    if (!orderHistoryList) {
        return;
    }

    const orderHistory = loadOrderHistory();

    currentUser.orderHistory = orderHistory;
    orderHistoryList.textContent = "";

    if (orderHistory.length === 0) {
        const emptyItem = document.createElement("li");
        emptyItem.textContent = "No orders yet.";
        orderHistoryList.appendChild(emptyItem);
        return;
    }

    orderHistory.forEach(function(order) {
        const listItem = document.createElement("li");
        const orderTitle = document.createElement("strong");
        const details = document.createElement("details");
        const summary = document.createElement("summary");
        const detailsContent = document.createElement("div");
        const itemsList = document.createElement("ul");

        orderTitle.textContent = "Order no." + order.id;
        summary.textContent = "Click to view the details";
        detailsContent.classList.add("order-details");

        detailsContent.innerHTML =
            "<p>Date: " + order.date + "</p>" +
            "<p>Status: " + order.status + "</p>" +
            "<p>Subtotal: " + formatPrice(order.subtotal) + "</p>" +
            "<p>Shipping: " + formatPrice(order.shipping) + "</p>" +
            "<p>Discount: - " + formatPrice(order.discount) + "</p>" +
            "<p>Total: " + formatPrice(order.total) + "</p>" +
            "<p>Items:</p>";

        order.items.forEach(function(item) {
            const itemRow = document.createElement("li");

            if (typeof item === "string") {
                itemRow.textContent = item;
            } else {
                itemRow.textContent =
                    item.name +
                    " | Qty: " +
                    item.quantity +
                    " | Price: " +
                    formatPrice(item.price) +
                    " | Subtotal: " +
                    formatPrice(item.subtotal);
            }

            itemsList.appendChild(itemRow);
        });

        detailsContent.appendChild(itemsList);
        details.appendChild(summary);
        details.appendChild(detailsContent);

        listItem.appendChild(orderTitle);
        listItem.appendChild(document.createTextNode(" - " + order.status));
        listItem.appendChild(details);
        orderHistoryList.appendChild(listItem);
    });
}

renderOrderHistory();

const thankyouOrderIdValue = document.querySelector("#thankyou-order-id-value");

if (thankyouOrderIdValue) {
    const latestOrder = loadLastOrder();

    if (latestOrder) {
        thankyouOrderIdValue.textContent = latestOrder.id;
    }
}

async function initializeApp() {
    products = await fetchProducts();
    renderLandingProducts();
    initializeLandingHeroSlider();
    renderProductGrid();
    renderCart();
    updateCartLinks();
}

initializeApp();
