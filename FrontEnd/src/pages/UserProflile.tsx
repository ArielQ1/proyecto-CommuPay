import { NavBar } from '../components/NavBar'
import logo from '../assets/commuPayLogo.png'

export function UserProfile () {
  return (
    <div className='bg-gray-950 min-h-screen'>
      <NavBar />
      <div className='flex justify-center p-3'>
        <section className='container w-1/4 '>
          <h1 className='text-3xl font-bold text-white mb-6'>User Profile & Wallet</h1>
          <div className='bg-gray-900 p-6 rounded-lg shadow-lg max-w-96 mx-auto'>
            <img src={logo} alt='' className='mx-auto w-52 h-52 bg-amber-50 rounded-full' />
            <h2 className='text-center text-white text-xl font-bold'>Sophia Carter</h2>
            <p className='text-center text-gray-400 mb-4'>
              sophia@gmail.com
            </p>
            <button className='bg-gray-700 text-white rounded-lg w-full py-3 font-semibold hover:bg-gray-600 transition-colors duration-200 hover:cursor-pointer'>
              Account Settings
            </button>
          </div>
        </section>
        <section className='w-1/2'>
          <div className='container p-6'>
            <h2 className='text-2xl font-bold text-white mb-4'>Wallet Balance</h2>
            <div className='bg-gray-900 p-6 rounded-lg shadow-lg '>
              <p className='text-gray-400 mb-2'>Current Balance</p>
              <p className='text-3xl font-bold text-white mb-4'>$1,250.00</p>
            </div>
          </div>
          <div className='container p-6'>
            <h2 className='text-2xl font-bold text-white mb-4'>Recent Transactions</h2>
            <div className='bg-gray-900 p-6 rounded-lg shadow-lg '>
              <ul className='space-y-4'>
                <li className='flex justify-between items-center'>
                  <div>
                    <p className='text-white font-semibold'>Payment to John Doe</p>
                    <p className='text-gray-400 text-sm'>Aug 15, 2023</p>
                  </div>
                  <p className='text-red-500 font-bold'>-$50.00</p>
                </li>
                <li className='flex justify-between items-center'>
                  <div>
                    <p className='text-white font-semibold'>Received from Jane Smith</p>
                    <p className='text-gray-400 text-sm'>Aug 14, 2023</p>
                  </div>
                  <p className='text-green-500 font-bold'>+$200.00</p>
                </li>
                <li className='flex justify-between items-center'>
                  <div>
                    <p className='text-white font-semibold'>Payment to Grocery Store</p>
                    <p className='text-gray-400 text-sm'>Aug 13, 2023</p>
                  </div>
                  <p className='text-red-500 font-bold'>-$75.00</p>
                </li>
              </ul>
            </div>
          </div>
        </section>
      </div>

    </div>
  )
}
