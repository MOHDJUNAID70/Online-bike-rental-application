package com.automation;

import com.microsoft.playwright.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();

            // Navigate to Royal Brothers website
            page.navigate("https://www.royalbrothers.com/");
            page.waitForTimeout(5000); // Wait for page load

            // Select city
            page.waitForSelector("input[placeholder='Search city']").click();
            page.fill("input[placeholder='Search city']", "Bangalore");
            // Wait for suggestions to appear and click the first one
            page.waitForSelector("div[class*='suggestion']");
            page.locator("div[class*='suggestion']").first().click();

            // Enter booking time
            // Pickup date
            page.waitForSelector("input[placeholder='Pickup Date']").click();
            page.fill("input[placeholder='Pickup Date']", "22 May, 2025");
            page.keyboard().press("Enter");

            // Pickup time
            page.waitForSelector("input[placeholder='Pickup Time']").click();
            page.fill("input[placeholder='Pickup Time']", "8:30 AM");
            page.keyboard().press("Enter");

            // Dropoff date
            page.waitForSelector("input[placeholder='Dropoff Date']").click();
            page.fill("input[placeholder='Dropoff Date']", "24 May, 2025");
            page.keyboard().press("Enter");

            // Dropoff time
            page.waitForSelector("input[placeholder='Dropoff Time']").click();
            page.fill("input[placeholder='Dropoff Time']", "9:30 AM");
            page.keyboard().press("Enter");

            // Click search
            page.waitForSelector("button:has-text('Search')").click();
            page.waitForTimeout(3000); // Wait for results

            // Apply location filter
            page.waitForSelector("input[placeholder='Search location']").click();
            page.fill("input[placeholder='Search location']", "Koramangala");
            // Wait for suggestions to appear and click the first one
            page.waitForSelector("div[class*='suggestion']");
            page.locator("div[class*='suggestion']").first().click();

            // Print available bikes
            page.waitForSelector("div[class*='bike-card']");
            Locator bikeModels = page.locator("div[class*='bike-name']");
            Locator locations = page.locator("div[class*='location']");

            System.out.println("Available Bikes:");
            for (int i = 0; i < bikeModels.count(); i++) {
                String model = bikeModels.nth(i).textContent();
                String availableAt = locations.nth(i).textContent();
                System.out.println("Bike Model: " + model + " | Available At: " + availableAt);
            }

            // Keep browser open for 10 seconds to see results
            page.waitForTimeout(10000);
            browser.close();
        }
    }
}
