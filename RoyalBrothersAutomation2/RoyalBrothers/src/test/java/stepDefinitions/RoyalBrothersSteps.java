package stepDefinitions;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.cucumber.java.en.*;
import org.testng.Assert;
import io.cucumber.java.After;

public class RoyalBrothersSteps {
    private Playwright playwright;
    private Browser browser;
    private Page page;

    @Given("I navigate to {string}")
    public void i_navigate_to(String url) {
        playwright = Playwright.create();
        // Launch browser in non-headless mode for visibility during development
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate(url);
        // Wait for the network to be idle after navigation
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @When("I select the city {string}")
    public void i_select_the_city(String city) {
        // Wait for the city input field to be visible and clickable
        Locator cityInput = page.locator("input[placeholder='Search city']");
        cityInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        cityInput.click();

        // Type the city name into the input field
        cityInput.fill(city);

        // Wait for the suggestion list/dropdown to appear after typing and click the first match
        Locator suggestion = page.locator("div[class*='suggestion'], li[class*='suggestion']:has-text('" + city + "')").first();
        suggestion.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        suggestion.click();
         // Wait for the page to settle after city selection might trigger updates
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @When("I enter the booking time from {string} at {string} to {string} at {string}")
    public void i_enter_the_booking_time(String pickupDate, String pickupTime, String dropoffDate, String dropoffTime) {
        // Select pickup date - use force: true to bypass potential overlay issues
        page.locator("input[placeholder='Pickup Date']")
            .fill(pickupDate, new Locator.FillOptions().setForce(true));
        // Press Escape to close the date picker if it appears and covers other elements
        page.press("input[placeholder='Pickup Date']", "Escape");

        // Select pickup time
        page.locator("input[placeholder='Pickup Time']")
            .fill(pickupTime, new Locator.FillOptions().setForce(true));
        page.press("input[placeholder='Pickup Time']", "Escape");

        // Select dropoff date
        page.locator("input[placeholder='Dropoff Date']")
            .fill(dropoffDate, new Locator.FillOptions().setForce(true));
        page.press("input[placeholder='Dropoff Date']", "Escape");

        // Select dropoff time
        page.locator("input[placeholder='Dropoff Time']")
            .fill(dropoffTime, new Locator.FillOptions().setForce(true));
        page.press("input[placeholder='Dropoff Time']", "Escape");
    }

    @When("I click on search")
    public void i_click_on_search() {
        // Playwright automatically waits for the search button to be enabled and visible before clicking
        page.locator("button:has-text('Search')").click();
        // Wait for search results to load (network idle is a good indicator)
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    // Modified step to accept date/time parameters and validate against them
    @Then("I should see the correct booking filter applied for pickup {string} at {string} and dropoff {string} at {string}")
    public void i_should_see_the_correct_booking_filter_applied(String expectedPickupDate, String expectedPickupTime, String expectedDropoffDate, String expectedDropoffTime) {
        // Wait for the filter elements to be visible on the page
        page.waitForSelector("div[class*='filter']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        // Get the actual values from the input fields
        String actualPickupDate = page.locator("input[placeholder='Pickup Date']").inputValue();
        String actualPickupTime = page.locator("input[placeholder='Pickup Time']").inputValue();
        String actualDropoffDate = page.locator("input[placeholder='Dropoff Date']").inputValue();
        String actualDropoffTime = page.locator("input[placeholder='Dropoff Time']").inputValue();

        // Assert that the actual values match the expected values passed from the feature file
        Assert.assertEquals(actualPickupDate, expectedPickupDate, "Pickup Date filter is incorrect.");
        Assert.assertEquals(actualPickupTime, expectedPickupTime, "Pickup Time filter is incorrect.");
        Assert.assertEquals(actualDropoffDate, expectedDropoffDate, "Dropoff Date filter is incorrect.");
        Assert.assertEquals(actualDropoffTime, expectedDropoffTime, "Dropoff Time filter is incorrect.");
    }

    @When("I apply the location filter {string}")
    public void i_apply_the_location_filter(String location) {
        // Wait for the location input to be visible and click it
        Locator locationInput = page.locator("input[placeholder='Search location']");
        locationInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        locationInput.click();

        // Type the location
        locationInput.fill(location);

        // Wait for suggestions to appear and click the first one that matches the location text
        Locator suggestion = page.locator("div[class*='suggestion']:has-text('" + location + "')").first();
         suggestion.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        suggestion.click();
        // Wait for the page to settle after location selection
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Then("I should only see bikes available at {string}")
    public void i_should_only_see_bikes_available_at(String location) {
        // Wait for bike listings to load and be visible
        page.waitForSelector("div[class*='bike-card']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));

        Locator locations = page.locator("div[class*='location']");

        // Use Playwright's count() and assert that there are bikes listed
        int count = locations.count();
        Assert.assertTrue(count > 0, "No bikes found at location: " + location);

        // Check each listed bike's location
        for (int i = 0; i < count; i++) {
            String locationText = locations.nth(i).textContent();
            Assert.assertTrue(locationText.contains(location),
                "Bike at index " + i + " is not at the expected location: " + location);
        }
    }

    @Then("I print all available bike models and locations")
    public void i_print_all_available_bike_models_and_locations() {
        // Wait for bike listings to load
        page.waitForSelector("div[class*='bike-card']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));

        Locator bikeModels = page.locator("div[class*='bike-name']");
        Locator locations = page.locator("div[class*='location']");

        System.out.println("Available Bikes:");
        // Iterate through the bikes and print their model and location
        for (int i = 0; i < bikeModels.count(); i++) {
            String model = bikeModels.nth(i).textContent();
            String availableAt = locations.nth(i).textContent();
            System.out.println("Bike Model: " + model + " | Available At: " + availableAt);
        }
    }

    @After
    public void tearDown() {
        // Close the browser and playwright instance after each scenario
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}

