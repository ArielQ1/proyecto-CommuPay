import logo from '../assets/commuPayLogo.png'

export function Home () {
  return (
    <div className='relative h-screen'>
      <div className='absolute inset-0'>
        <div className='absolute top-0 z-[-2] h-screen w-screen bg-[#000000] bg-[radial-gradient(#ffffff33_1px,#00091d_1px)] bg-[size:20px_20px]' />
      </div>

      <div className='relative z-10 flex h-full flex-col items-center justify-center px-4'>
        <div className='max-w-3xl text-center'>
          <h1 className='mb-4 text-7xl font-bold tracking-tight text-white'>
            Welcome to
            <span className='text-sky-400'> CommuPay</span>
          </h1>
          <img className='w-40 h-40 mx-auto my-1' src={logo} alt='' />
          <form className='w-lg border shadow-2xl border-sky-300/30 rounded-xl mb-2 mx-auto p-8 bg-black/20 backdrop-blur-md hover:bg-black/25 transition-all duration-300'>
            <h1 className='mb-8 text-3xl font-bold tracking-tight text-white drop-shadow-lg'>
              Sign in to Your Account
            </h1>
            <div className='mb-6 px-6'>
              <label htmlFor='email' className='mb-3 block text-left font-medium text-white/90 text-sm uppercase tracking-wide'>
                Email Address
              </label>
              <input
                id='email'
                type='email'
                placeholder='Enter your email'
                className='w-full rounded-lg border border-white/20 bg-white/10 backdrop-blur-sm px-4 py-3 text-white placeholder-white/60 focus:border-sky-400 focus:outline-none focus:ring-2 focus:ring-sky-400/50 transition-all duration-200'
              />
            </div>
            <div className='mb-8 px-6'>
              <label htmlFor='password' className='mb-3 block text-left font-medium text-white/90 text-sm uppercase tracking-wide'>
                Password
              </label>
              <input
                id='password'
                type='password'
                placeholder='Enter your password'
                className='w-full rounded-lg border border-white/20 bg-white/10 backdrop-blur-sm px-4 py-3 text-white placeholder-white/60 focus:border-sky-400 focus:outline-none focus:ring-2 focus:ring-sky-400/50 transition-all duration-200'
              />
            </div>
            <div className='px-6 pb-2'>
              <button
                type='submit'
                className='w-full rounded-lg bg-gradient-to-r from-sky-600 to-sky-700 px-4 py-3 font-semibold text-white shadow-lg hover:from-sky-700 hover:to-sky-800 focus:outline-none focus:ring-2 focus:ring-sky-500 focus:ring-offset-2 focus:ring-offset-transparent hover:cursor-pointer transform hover:scale-[1.02] transition-all duration-200'
              >
                Sign In
              </button>
            </div>
          </form>

        </div>
      </div>
    </div>
  )
}
