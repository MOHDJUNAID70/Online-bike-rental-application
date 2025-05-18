Feature: Bike Rental Booking on RoyalBrothers Website

  Scenario Outline: Search and validate bike rental availability for different cities and locations
    Given I navigate to "https://www.royalbrothers.com/"
    When I select the city "<City>"
    And I enter the booking time from "<PickupDate>" at "<PickupTime>" to "<DropoffDate>" at "<DropoffTime>"
    And I click on search
    Then I should see the correct booking filter applied for pickup "<PickupDate>" at "<PickupTime>" and dropoff "<DropoffDate>" at "<DropoffTime>"
    When I apply the location filter "<Location>"
    Then I should only see bikes available at "<Location>"
    Then I print all available bike models and locations

    Examples:
      | City      | PickupDate   | PickupTime | DropoffDate  | DropoffTime | Location    |
      | Bangalore | 22 May, 2025 | 8:30 AM    | 24 May, 2025 | 9:30 AM     | Koramangala |
      # Add more examples here for different cities, dates, times, and locations

