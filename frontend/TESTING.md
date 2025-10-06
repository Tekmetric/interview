# Testing Documentation

This document outlines the comprehensive testing setup for the Pokédex application.

## Testing Infrastructure

### 1. Unit Testing (Jest + React Testing Library)
- **Framework**: Jest with React Testing Library
- **Location**: `src/**/*.test.js`
- **Run**: `npm test`

#### Test Coverage
- Component tests (ErrorBoundary, DarkModeToggle)
- Utility function tests (data.test.js)
- Integration tests (App.integration.test.js)

### 2. Integration Testing
- **File**: `src/App.integration.test.js`
- **Tests**:
  - Pokemon data loading and display
  - Search functionality (by name, number, type)
  - Dark mode toggle persistence
  - Language switching across all 5 languages
  - Responsive behavior

### 3. End-to-End Testing (Playwright)
- **Framework**: Playwright
- **Location**: `e2e/*.spec.js`
- **Config**: `playwright.config.js`

#### Run Commands
```bash
# Run e2e tests
npm run test:e2e

# Run with UI mode (interactive)
npm run test:e2e:ui

# Run in headed mode (see browser)
npm run test:e2e:headed
```

#### E2E Test Coverage
**Functional Tests** (`e2e/pokedex.spec.js`):
- Application loads correctly
- Pokemon data displays
- Search filters work
- Pokemon links are correct
- Dark mode toggles and persists
- Language switching works
- Keyboard navigation
- Responsive design (mobile, tablet, desktop)

**Accessibility Tests**:
- Automated accessibility scanning with Axe
- Keyboard navigation support
- ARIA labels and roles

### 4. Visual Regression Testing
- **Framework**: Playwright Screenshots
- **Location**: `e2e/visual-regression.spec.js`

#### Visual Test Coverage
- Homepage (light/dark mode)
- Search results
- Mobile viewport
- Tablet viewport
- Different languages
- Component snapshots (header)
- Loading states
- Error states

#### Running Visual Tests
```bash
# Generate baseline screenshots
npm run test:e2e -- visual-regression

# Update snapshots when changes are intentional
npm run test:e2e -- --update-snapshots
```

### 5. Component Development (Storybook)
- **Framework**: Storybook 9
- **Location**: `src/components/*.stories.js`

#### Run Commands
```bash
# Start Storybook
npm run storybook

# Build static Storybook
npm run build-storybook
```

#### Available Stories
- **DarkModeToggle**: Default, InHeader, DarkMode variants
- **LanguageSwitcher**: Default, InHeader, WithDarkModeToggle
- **ErrorBoundary**: NoError, WithError, CustomFallback
- **BarChart**: Default, HighStats, NoStats
- **TableHeader**: Desktop, Mobile, DarkMode

## Test Scripts

| Command | Description |
|---------|-------------|
| `npm test` | Run unit and integration tests |
| `npm run test:e2e` | Run end-to-end tests |
| `npm run test:e2e:ui` | Run e2e tests in interactive UI mode |
| `npm run test:e2e:headed` | Run e2e tests with visible browser |
| `npm run storybook` | Start Storybook component explorer |
| `npm run build-storybook` | Build static Storybook |

## Testing Best Practices

### Unit Tests
- Test individual component behavior
- Mock external dependencies
- Test props and state changes
- Verify rendering logic

### Integration Tests
- Test component interaction
- Verify data flow
- Test user workflows
- Check state management

### E2E Tests
- Test complete user journeys
- Verify real browser behavior
- Test across viewports
- Check accessibility

### Visual Regression
- Capture UI screenshots
- Compare against baselines
- Detect unintended visual changes
- Test responsive layouts

## CI/CD Integration

### GitHub Actions (Recommended)
```yaml
- name: Run tests
  run: npm test -- --coverage --watchAll=false

- name: Run e2e tests
  run: npm run test:e2e

- name: Upload coverage
  uses: codecov/codecov-action@v3
```

### Pre-commit Hooks
The project uses Husky for pre-commit linting:
- ESLint runs on staged files
- Maximum 0 warnings allowed

## Coverage Goals

Current coverage:
- **Unit Tests**: ~70% statement coverage
- **Integration Tests**: Core user flows
- **E2E Tests**: Critical paths
- **Visual Tests**: Major UI states

## Adding New Tests

### Unit Test Template
```javascript
import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import MyComponent from './MyComponent';

describe('MyComponent', () => {
  it('renders correctly', () => {
    const { getByText } = render(<MyComponent />);
    expect(getByText('Hello')).toBeInTheDocument();
  });
});
```

### E2E Test Template
```javascript
const { test, expect } = require('@playwright/test');

test('user can complete task', async ({ page }) => {
  await page.goto('/');
  // Add test steps
});
```

### Storybook Story Template
```javascript
import MyComponent from './MyComponent';

export default {
  title: 'Components/MyComponent',
  component: MyComponent,
};

export const Default = {
  args: {
    prop1: 'value',
  },
};
```

## Troubleshooting

### Tests Timing Out
- Increase timeout in `playwright.config.js`
- Check network requests
- Verify selectors are correct

### Visual Tests Failing
- Review screenshot diffs
- Update baselines if changes are intentional
- Check for animation timing issues

### Storybook Not Loading
- Clear cache: `rm -rf node_modules/.cache`
- Rebuild: `npm run build-storybook`

## Resources

- [Jest Documentation](https://jestjs.io/)
- [React Testing Library](https://testing-library.com/react)
- [Playwright Documentation](https://playwright.dev/)
- [Storybook Documentation](https://storybook.js.org/)
- [Axe Accessibility Testing](https://www.deque.com/axe/)
