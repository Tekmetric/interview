import { Page, Locator } from "@playwright/test";

/**
 * Product information type
 */
export interface Product {
  name: string;
  price: string;
  priceNumber: number; // Price as number (e.g., 500 from "Rs. 500")
  imageSrc?: string;
}

/**
 * ProductsPage - Handles the products listing page
 * URL: /products
 */
export class ProductsPage {
  readonly page: Page;

  // Page heading
  readonly allProductsHeading: Locator;

  // Product containers
  readonly productCards: Locator;

  // Search
  readonly searchInput: Locator;
  readonly searchButton: Locator;

  constructor(page: Page) {
    this.page = page;

    this.allProductsHeading = page.getByRole("heading", {
      name: "All Products",
    });
    this.productCards = page.locator(".productinfo"); // Each product card
    this.searchInput = page.locator("#search_product");
    this.searchButton = page.locator("#submit_search");
  }

  /**
   * Navigate to products page
   */
  async goto() {
    await this.page.goto("/products");
    await this.allProductsHeading.waitFor({ state: "visible" });
  }

  /**
   * Get all product cards on the page
   */
  async getProductCards(): Promise<Locator[]> {
    return await this.productCards.all();
  }

  /**
   * Get product name from a product card
   */
  async getProductName(productCard: Locator): Promise<string> {
    const nameElement = productCard.locator("p");
    return (await nameElement.textContent())?.trim() || "";
  }

  /**
   * Get product price from a product card (e.g., "Rs. 500")
   */
  async getProductPrice(productCard: Locator): Promise<string> {
    const priceElement = productCard.locator("h2");
    return (await priceElement.textContent())?.trim() || "";
  }

  /**
   * Get product price as number (e.g., 500 from "Rs. 500")
   */
  async getProductPriceAsNumber(productCard: Locator): Promise<number> {
    const priceText = await this.getProductPrice(productCard);
    const priceMatch = priceText.match(/\d+/);
    return priceMatch ? parseInt(priceMatch[0]) : 0;
  }

  /**
   * Get product image source
   */
  async getProductImageSrc(productCard: Locator): Promise<string> {
    const imageElement = productCard.locator("img");
    const src = (await imageElement.getAttribute("src")) || "";
    return src.startsWith("/") ? src.substring(1) : src;
  }

  /**
   * Get "Add to cart" button for a product card
   */
  getAddToCartButton(productCard: Locator): Locator {
    return productCard.locator("a.btn.btn-default.add-to-cart");
  }

  /**
   * Click "Add to cart" button for a product card
   */
  async addProductToCart(productCard: Locator): Promise<void> {
    const addToCartBtn = this.getAddToCartButton(productCard);
    await addToCartBtn.click();
  }

  /**
   * Get all products as an array of Product objects
   * @returns Array of products with name, price, numeric price, and image src
   */
  async getAllProducts(): Promise<Product[]> {
    const cards = await this.getProductCards();
    const products: Product[] = [];

    for (const card of cards) {
      const name = await this.getProductName(card);
      const price = await this.getProductPrice(card);
      const priceNumber = await this.getProductPriceAsNumber(card);
      const imageSrc = await this.getProductImageSrc(card);

      products.push({ name, price, priceNumber, imageSrc });
    }

    return products;
  }

  /**
   * Get total number of products on page
   */
  async getProductCount(): Promise<number> {
    return await this.productCards.count();
  }

  /**
   * Find product by name
   * @param productName - Exact or partial product name
   * @returns Product card locator or null if not found
   */
  async findProductByName(productName: string): Promise<Locator | null> {
    const cards = await this.getProductCards();

    for (const card of cards) {
      const name = await this.getProductName(card);
      if (name.toLowerCase().includes(productName.toLowerCase())) {
        return card;
      }
    }

    return null;
  }

  /**
   * Add product to cart by product name
   * @param productName - Product name to search for
   * @returns true if product found and added, false otherwise
   */
  async addProductToCartByName(productName: string): Promise<boolean> {
    const productCard = await this.findProductByName(productName);

    if (productCard) {
      await this.addProductToCart(productCard);
      return true;
    }

    return false;
  }

  /**
   * Get product at specific index (0-based)
   */
  getProductCardByIndex(index: number): Locator {
    return this.productCards.nth(index);
  }

  /**
   * Get product information by index
   * @param index - 0-based index
   */
  async getProductByIndex(index: number): Promise<Product> {
    const card = this.getProductCardByIndex(index);
    const name = await this.getProductName(card);
    const price = await this.getProductPrice(card);
    const priceNumber = await this.getProductPriceAsNumber(card);
    const imageSrc = await this.getProductImageSrc(card);

    return { name, price, priceNumber, imageSrc };
  }

  /**
   * Select a random product from visible products on the page
   * @returns Random product with name and price, or null if no products
   */
  async getRandomProduct(): Promise<Product | null> {
    const products = await this.getAllProducts();

    if (products.length === 0) {
      return null;
    }

    const randomIndex = Math.floor(Math.random() * products.length);
    return products[randomIndex];
  }

  /**
   * Add a random product to cart
   * @returns The product that was added, or null if no products available
   */
  async addRandomProductToCart(): Promise<Product | null> {
    const productCount = await this.getProductCount();

    if (productCount === 0) {
      return null;
    }

    const randomIndex = Math.floor(Math.random() * productCount);
    const productCard = this.getProductCardByIndex(randomIndex);

    // Get product info before adding to cart
    const product = await this.getProductByIndex(randomIndex);

    // Add to cart
    await this.addProductToCart(productCard);

    return product;
  }

  /**
   * Hover over a product card
   * @param productCard - Product card locator
   */
  async hoverOverProductCard(productCard: Locator): Promise<void> {
    await productCard.hover();
  }

  /**
   * Hover over product card by index
   * @param index - 0-based index of product
   */
  async hoverOverProductByIndex(index: number): Promise<void> {
    const productCard = this.getProductCardByIndex(index);
    await this.hoverOverProductCard(productCard);
  }

  /**
   * Hover over a random product card
   * @returns The index of the product that was hovered
   */
  async hoverOverRandomProduct(): Promise<number> {
    const productCount = await this.getProductCount();

    if (productCount === 0) {
      return -1;
    }

    const randomIndex = Math.floor(Math.random() * productCount);
    await this.hoverOverProductByIndex(randomIndex);

    return randomIndex;
  }

  /**
   * Hover over random product card and add to cart
   * @returns The product that was added, or null if no products available
   */
  async hoverAndAddRandomProductToCart(): Promise<Product | null> {
    const productCount = await this.getProductCount();

    if (productCount === 0) {
      return null;
    }

    const randomIndex = Math.floor(Math.random() * productCount);
    const productCard = this.getProductCardByIndex(randomIndex);

    // Get product info
    const product = await this.getProductByIndex(randomIndex);

    // Hover over product card
    await this.hoverOverProductCard(productCard);

    // Wait a moment for hover effects
    await this.page.waitForTimeout(500);

    // Add to cart
    await this.addProductToCart(productCard);

    return product;
  }
}
