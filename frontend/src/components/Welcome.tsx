export default function Welcome() {
  return (
    <div className="bg-sky-100 p-4 m-4 shadow-md rounded-md">
      <header className="text-xl">
        <h2>Welcome to the interview app!</h2>
      </header>
      <p>
        Edit
        { ' ' }
        <code>src/App.js</code>
        { ' ' }
        and save to reload.
      </p>
      <ol className="list-decimal p-4">
        <li>
          Fetch Data from a public API:
          { ' ' }
          <a
            className="text-blue-500 hover:underline"
            href="https://github.com/toddmotto/public-apis"
          >
            Samples
          </a>
        </li>
        <li>Display data from API onto your page (Table, List, etc.)</li>
        <li>
          Apply a styling solution of your choice to make your page look different (CSS, SASS,
          CSS-in-JS)
        </li>
      </ol>
    </div>
  );
}
