# Transaction-Monitor-App
Android mBanking application for retrieving user transaction data from an API

### Requirements:
- The user will be able to register, log in and look up transactions for any of their accounts

- Registration
  - The screen should contain fields for entering the user's name and surname
  - After entering their data, the user will have to define their PIN code
  - PIN entry is done via a screen with a single entry field and a custom numeric keyboard
  - If the PIN is successfuly entered, data is fetched from the API endpoint
  - Notice: 
    - All fields are mandatory
    - Name and surname must contain only alphanumeric characters, and cannot be longer than 30 chars
    - PIN must have a minimum of four and a maximum of six characters
- Login
  - The login screen should contain a title with the name and surname of the user (if previously registered)
  - If the user clicks on the Login button, a dialog for entering the PIN is displayed
  - If the PIN is correct, the main screen is opened
  
- Main
  - The main screen contains a dialog where a user can choose for which account they want to display transactions
  - If an account is chosen, the transactions are listed below the dialog
  - Notice:
    - Enable the user to logout (to the login screen)
    - Sort transactions by date

## Tech stack
- MVVM architecture
- Fragments
- Dagger Hilt
- LiveData
- Coroutines
- RecyclerView
- Retrofit
- Timber

## Lessons learned
1. **Scaling images in XML**
   - If we want to scale an image, we should set the height or width to the desired size, and add the adjustViewBounds attribute to true. This will resize the image and keep the aspect ratio
2. **Button color not changing**
   - The register button on the Login screen didn't change to the color set in its selector, but instead displayed the primary app color.<br>
   I managed to resolve this by using the "DarkActionBar.Bridge" parent theme, instead of "DarkActionBar"
3. **Spinner**
   - First time working with Spinners to display a list of possibilities
4. **Navigation component**
   - While making the app, I used Jetpack's Navigation components library for the first time


## Future plans
1. **Fix login buttons**
   - On the login screen, buttons for login and registration are for some reason not working. The code in their onClickListeners is not being executed when the button is pressed 
2. **Complete code in main fragment**
   - Complete code so that the data from the API is correctly displayed in the Spinner and RecyclerView
