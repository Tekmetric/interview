import React from 'react'

function App() {
  return (
    <div className='w-full flex flex-col justify-center items-center'>
      <h2 className='m-10'>Welcome to the interview app!</h2>
      <p>
        Edit <code>src/App.js</code> and save to reload.
      </p>

      <ul className='m-10'>
        <li>
          Fetch Data from a public API{' '}
          <a href='https://github.com/toddmotto/public-apis'>Samples</a>
        </li>
        <li>Display data from API onto your page (Table, List, etc.)</li>
        <li>
          Apply a styling solution of your choice to make your page look different (CSS, SASS,
          CSS-in-JS)
        </li>
      </ul>
    </div>
  )
}

export default App
