import { Route, Routes } from 'react-router-dom'
import { Home } from './pages/Home'
import { UserProfile } from './pages/UserProflile'

function App () {
  return (
    <Routes>
      <Route path='/' element={<Home />} />
      <Route path='/profile' element={<UserProfile />} />
    </Routes>
  )
}

export default App
