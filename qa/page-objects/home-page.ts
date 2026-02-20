import { Page, Locator } from "@playwright/test";

export class HomePage {

  constructor(private page: Page) {}

  async goto() {
    await this.page.goto("/");
  }
  async bookRoom(roomName: string) {
    const targetRoomCard = this.page.locator(".room-card").filter({
      has: this.page.getByRole("heading", { name: roomName }),
    });
    await targetRoomCard.getByRole("link", { name: "Book now" }).click();
  }
}
