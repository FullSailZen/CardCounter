package com.example.cardcounter;

import android.app.Application;

/**
 * Minimal custom Application class for Robolectric unit tests.
 *
 * We keep it lightweight and avoid any UI/theme setup that could cause
 * Resource/Inflation errors in the test environment.
 */
public class TestApp extends Application {
    // constructor is required for Robolectric
    public TestApp() {
        super();
    }
}