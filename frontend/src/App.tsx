import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import RecipeDetailsPage from './pages/recipe_details_page'
import RecipesPage from './pages/recipes_page'

const App = () => (
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<RecipesPage />} />
      <Route path="/:id" element={<RecipeDetailsPage />} />
      <Route path="*" element={<Navigate to={'/'} />} />
    </Routes>
  </BrowserRouter>
)

export default App
