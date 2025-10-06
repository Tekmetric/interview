#!/bin/bash
# Script to reorganize all commits into atomic chunks from master branch

set -e

echo "⚠️  This will reset ALL commits on this branch back to master and recreate them atomically."
echo "Current branch commits (7 total):"
git log master..HEAD --oneline
echo ""
read -p "Continue? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

# Reset to master
echo "📦 Resetting to master branch..."
git reset --soft master

# Unstage everything
git reset

echo ""
echo "✨ Creating atomic commits from master..."
echo ""

# 1. Initial app structure and styling
echo "1️⃣  Initial Pokedex app setup..."
git add frontend/src/App.js \
  frontend/src/index.css \
  frontend/public/pokeball.svg \
  frontend/README.md

git commit -m "Initial Pokedex app setup

- Create React app with Pokemon data display
- Add basic styling and layout
- Add Pokeball logo"

# 2. Add Tailwind CSS
echo "2️⃣  Adding Tailwind CSS..."
git add frontend/tailwind.config.js \
  frontend/postcss.config.js

git commit -m "Add Tailwind CSS configuration

- Configure Tailwind for styling
- Set up PostCSS"

# 3. Refactor into reusable components
echo "3️⃣  Refactoring into components..."
git add frontend/src/components/Table.js \
  frontend/src/components/TableHeader.js \
  frontend/src/components/TableBody.js \
  frontend/src/components/TableCell.js \
  frontend/src/components/BarChart.js

git commit -m "Refactor UI into reusable components

- Extract Table components for better organization
- Create BarChart component for stats visualization
- Improve code maintainability through separation of concerns"

# 4. Add centralized styles
echo "4️⃣  Adding centralized styles..."
git add frontend/src/lib/styles.js

git commit -m "Extract styles into centralized module

- Move styles from components to lib/styles.js
- Improve style reusability and consistency
- Better theme management"

# 5. Add utility functions
echo "5️⃣  Adding utility functions..."
git add frontend/src/lib/utils.js

git commit -m "Add utility functions for data conversion

- Height conversion (metric/imperial)
- Weight conversion (metric/imperial)
- String capitalization
- Locale-aware conversions"

# 6. Add data fetching module
echo "6️⃣  Adding data module..."
git add frontend/src/lib/data.js

git commit -m "Create data fetching module

- Centralize Pokemon API calls
- Add API health check
- Handle errors gracefully"

# 7. Add PropTypes
echo "7️⃣  Adding PropTypes..."
git add frontend/src/components/BarChart.js \
  frontend/src/components/Table.js \
  frontend/src/components/TableBody.js \
  frontend/src/components/TableCell.js \
  frontend/src/components/TableHeader.js

git commit -m "Add PropTypes for type safety

- Add runtime type checking to components
- Improve development experience
- Catch prop type errors early"

# 8. Add internationalization
echo "8️⃣  Adding i18n..."
git add frontend/src/i18n.js \
  frontend/src/locales/ \
  frontend/package.json \
  frontend/package-lock.json

git commit -m "Add internationalization (i18n) support

- Add react-i18next for multi-language support
- Support 5 languages: English, Spanish, Japanese, French, German
- Auto-detect browser language
- Persist language preference"

# 9. Add dark mode
echo "9️⃣  Adding dark mode..."
git add frontend/src/contexts/ThemeContext.js \
  frontend/src/components/DarkModeToggle.js

git commit -m "Add dark mode support

- Create ThemeContext for theme management
- Add DarkModeToggle component
- Persist theme in localStorage
- Detect system color scheme preference"

# 10. Add ErrorBoundary
echo "🔟 Adding ErrorBoundary..."
git add frontend/src/components/ErrorBoundary.js

git commit -m "Add ErrorBoundary for graceful error handling

- Catch React component errors
- Custom fallback UI
- Reset functionality to recover from errors"

# 11. Add LanguageSwitcher
echo "1️⃣1️⃣  Adding LanguageSwitcher..."
git add frontend/src/components/LanguageSwitcher.js

git commit -m "Add language switcher component

- Dropdown for language selection
- Display language flags
- Integrate with i18n"

# 12. Add keyboard navigation
echo "1️⃣2️⃣  Adding keyboard navigation..."
git add frontend/src/components/KeyboardNavigation.js

git commit -m "Add keyboard navigation for accessibility

- Arrow keys for scrolling
- Page Up/Down for faster navigation
- Home/End to jump to start/end
- Custom hook for reusability
- Improves accessibility compliance"

# 13. Integrate all features into App
echo "1️⃣3️⃣  Integrating features into App..."
git add frontend/src/App.js \
  frontend/src/index.js

git commit -m "Integrate all features into main app

- Add dark mode toggle and language switcher
- Wrap app in ErrorBoundary and ThemeProvider
- Position controls in top-right corner
- Add i18n translations throughout
- Suppress third-party library warnings"

# 14. Add caching system
echo "1️⃣4️⃣  Adding caching..."
git add frontend/src/lib/cache.js

git commit -m "Add localStorage caching utility

- Cache with TTL (24 hour default)
- Automatic expiration handling
- Quota exceeded error recovery
- Cache statistics and management
- Versioned cache keys for migrations"

# 15. Integrate caching
echo "1️⃣5️⃣  Integrating caching into data layer..."
git add frontend/src/lib/data.js

git commit -m "Add API caching to reduce network requests

- Check cache before fetching from API
- Store Pokemon data in localStorage
- Reduce data to essential fields only (~1-2MB)
- Add cache invalidation function
- Dramatically improve load times on repeat visits"

# 16. Add unit tests
echo "1️⃣6️⃣  Adding unit tests..."
git add frontend/src/lib/utils.test.js \
  frontend/src/components/BarChart.test.js \
  frontend/src/components/DarkModeToggle.test.js \
  frontend/src/components/ErrorBoundary.test.js \
  frontend/src/components/KeyboardNavigation.test.js

git commit -m "Add comprehensive unit tests

- Test utility functions
- Test BarChart with mocked chart library
- Test DarkModeToggle functionality
- Test ErrorBoundary error handling
- Test keyboard navigation
- Mock third-party dependencies"

# 17. Add component tests with high coverage
echo "1️⃣7️⃣  Adding more tests..."
git add frontend/src/lib/cache.test.js \
  frontend/src/contexts/ThemeContext.test.js \
  frontend/src/data.test.js

git commit -m "Add tests for cache, theme, and data modules

- Cache utility tests (96% coverage)
- ThemeContext tests (100% coverage)
- Data fetching tests
- Test error handling and edge cases"

# 18. Add App tests
echo "1️⃣8️⃣  Adding App tests..."
git add frontend/src/App.test.js

git commit -m "Add App component tests

- Test link functionality
- Test event handlers (resize, keyboard shortcuts)
- Test error states
- Test loading states"

# 19. Add integration tests
echo "1️⃣9️⃣  Adding integration tests..."
git add frontend/src/App.integration.test.js

git commit -m "Add integration tests

- End-to-end app testing
- Test dark mode toggle
- Test language switching
- Test component interactions"

# 20. Add E2E tests with Playwright
echo "2️⃣0️⃣  Adding E2E tests..."
git add frontend/e2e/ \
  frontend/playwright.config.js \
  frontend/TESTING.md

git commit -m "Add E2E tests with Playwright

- Test search functionality
- Test Pokemon display
- Test dark mode toggle
- Visual regression testing
- Add comprehensive testing documentation"

# 21. Add Storybook
echo "2️⃣1️⃣  Adding Storybook..."
git add frontend/.storybook/ \
  frontend/src/components/*.stories.js \
  frontend/src/stories/

git commit -m "Add Storybook for component development

- Configure Storybook with Webpack 5
- Add stories for all custom components
- Include default Storybook examples
- Improve component development workflow"

# 22. Add GitHub Actions
echo "2️⃣2️⃣  Adding CI/CD..."
git add frontend/.github/workflows/deploy.yml

git commit -m "Add GitHub Actions CI/CD pipeline

- Run tests on push/PR
- Run linter
- Deploy to GitHub Pages
- Automated quality checks"

# 23. Add pre-commit hooks
echo "2️⃣3️⃣  Adding pre-commit hooks..."
git add .husky/ \
  frontend/package.json

git commit -m "Add pre-commit hooks with Husky

- Run ESLint before commits
- Lint only staged files with lint-staged
- Enforce code quality
- Prevent committing broken code"

# 24. Configure lint and coverage
echo "2️⃣4️⃣  Configuring lint and coverage..."
git add frontend/.eslintignore \
  frontend/.gitignore \
  frontend/package.json

git commit -m "Configure linting and test coverage

- Add .eslintignore for build artifacts
- Configure coverage exclusions (stories, build)
- Add npm lint script
- Update .gitignore for build/coverage artifacts
- Configure lint-staged for src/ files only"

# 25. Update dependencies
echo "2️⃣5️⃣  Updating dependencies..."
git add frontend/package.json \
  frontend/package-lock.json \
  frontend/yarn.lock

git commit -m "Update project dependencies

- Add testing libraries (@playwright/test)
- Add i18n dependencies (react-i18next, i18next)
- Add Storybook dependencies
- Add Tailwind CSS
- Add Husky and lint-staged
- Update package locks"

echo ""
echo "✅ Done! Created 25 atomic commits:"
echo ""
git log --oneline -25
echo ""
echo "To review the changes:"
echo "  git log -p"
echo ""
echo "To push (force required since we rewrote history):"
echo "  git push --force-with-lease origin jonyen-coding-exercise"
