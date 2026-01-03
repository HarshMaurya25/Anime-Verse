import { useEffect, useMemo, useState } from 'react'
import { AnimatePresence } from 'framer-motion'
import * as Tooltip from '@radix-ui/react-tooltip'
import { Heart, MessageCircle, ThumbsDown, ThumbsUp, Bookmark, Sparkles } from 'lucide-react'
import accentOptions from '../theme/accentOptions.js'
import { getSavedAccentKey } from '../theme/accentStorage.js'
import NavigationBar from '../components/NavigationBar.jsx'
import LoadingScreen from '../components/LoadingScreen.jsx'

const seedPosts = [
  {
    id: 'p1',
    author: 'Aki Nakamura',
    handle: '@aki.n',
    anime: 'Jujutsu Kaisen',
    title: 'Domain expansions look unreal',
    description: 'Rewatching Shibuya and the staging still gives me chills. MAPPA snapped.',
    image: 'https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=1200&q=80',
    likes: 184,
    dislikes: 3,
    comments: 42,
    isFaved: false,
  },
  {
    id: 'p2',
    author: 'Rin Takahashi',
    handle: '@rin.draws',
    anime: 'Demon Slayer',
    title: 'Sound Hashira supremacy',
    description: 'The Tengen + Tanjiro rooftop fight is peak sakuga. Colors, music, everything.',
    image: 'https://images.unsplash.com/photo-1528715471579-d1bcf0ba5e83?auto=format&fit=crop&w=1200&q=80',
    likes: 231,
    dislikes: 5,
    comments: 58,
    isFaved: true,
  },
  {
    id: 'p3',
    author: 'Leo Martins',
    handle: '@leo.m',
    anime: 'Attack on Titan',
    title: 'Paths is still wild',
    description: 'Ymir backstory hits harder on rewatch. The score layering is immaculate.',
    image: 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1200&q=80',
    likes: 312,
    dislikes: 11,
    comments: 77,
    isFaved: false,
  },
  {
    id: 'p4',
    author: 'Mei Park',
    handle: '@mei.cos',
    anime: 'One Piece',
    title: 'Egghead is cooking',
    description: 'Chapter panels are straight-up sci-fi candy. Vegapunk is my spirit guide.',
    image: 'https://images.unsplash.com/photo-1526404746352-154fb34c402e?auto=format&fit=crop&w=1200&q=80',
    likes: 198,
    dislikes: 7,
    comments: 39,
    isFaved: false,
  },
  {
    id: 'p5',
    author: 'Kaito Sato',
    handle: '@kaito.vibes',
    anime: 'Chainsaw Man',
    title: 'Power best girl',
    description: 'Re-read and I still laugh at every Power+Denji bit. The chaos is art.',
    image: 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1200&q=80',
    likes: 267,
    dislikes: 9,
    comments: 64,
    isFaved: true,
  },
  {
    id: 'p6',
    author: 'Sara Imani',
    handle: '@sara.i',
    anime: 'My Hero Academia',
    title: 'Season 7 glow-up',
    description: 'The new lighting pass makes the set pieces feel movie-grade.',
    image: 'https://images.unsplash.com/photo-1477959858617-67f85cf4f1df?auto=format&fit=crop&w=1200&q=80',
    likes: 143,
    dislikes: 4,
    comments: 33,
    isFaved: false,
  },
  {
    id: 'p7',
    author: 'Noah Kim',
    handle: '@noah.codes',
    anime: 'Vinland Saga',
    title: 'Farmland arc appreciation',
    description: 'Peak character writing. Thorfinn growth + Einar patience = masterpiece.',
    image: 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=1200&q=80',
    likes: 189,
    dislikes: 2,
    comments: 51,
    isFaved: false,
  },
]

function Home() {
  const accentKey = getSavedAccentKey('crimson-night')
  const accent = useMemo(() => accentOptions.find((opt) => opt.key === accentKey)?.colors || accentOptions[0].colors, [accentKey])
  const [showAccentLoader, setShowAccentLoader] = useState(false)
  const [posts, setPosts] = useState(seedPosts)

  useEffect(() => {
    let timer = null
    const startLoader = () => {
      setShowAccentLoader(true)
      if (timer) clearTimeout(timer)
      timer = setTimeout(() => setShowAccentLoader(false), 2000)
    }

    const onStorage = (e) => {
      if (e.key === 'accentKey') startLoader()
    }

    const onCustom = (e) => {
      startLoader()
    }

    window.addEventListener('storage', onStorage)
    window.addEventListener('accent:changed', onCustom)
    return () => {
      window.removeEventListener('storage', onStorage)
      window.removeEventListener('accent:changed', onCustom)
      if (timer) clearTimeout(timer)
    }
  }, [])

  const toggleReaction = (id, key) => {
    setPosts((prev) =>
      prev.map((p) => {
        if (p.id !== id) return p
        const delta = key === 'likes' || key === 'dislikes' ? 1 : 0
        return {
          ...p,
          [key]: key === 'isFaved' ? !p.isFaved : p[key] + delta,
        }
      })
    )
  }

  return (
    <div
      className="relative min-h-screen overflow-visible text-slate-50"
      style={{ backgroundImage: `linear-gradient(135deg, ${accent.start}, ${accent.mid}, ${accent.end})` }}
    >
      <style>{`
        ::selection { background: ${accent.strong}; color: #f8fafc; }
        input::selection, textarea::selection { background: ${accent.strong}; color: #f8fafc; }
      `}</style>
      <div
        className="pointer-events-none absolute inset-0"
        style={{
          backgroundImage: `radial-gradient(circle at 45% 20%, ${accent.glow}, transparent 38%), radial-gradient(circle at 78% 30%, rgba(255,255,255,0.06), transparent 30%), radial-gradient(circle at 55% 78%, rgba(255,255,255,0.04), transparent 33%)`,
          mixBlendMode: 'screen',
        }}
      />

      <div className="relative z-10 px-4 pb-16 pt-14 sm:px-8 lg:px-12 xl:px-16">
        <header className="flex flex-wrap items-center justify-between gap-4">
          <div>
            <p className="text-xs uppercase tracking-[0.25em] text-slate-300/80">ThatOtakuNetwork</p>
            <h1 className="mt-2 text-4xl font-semibold text-white">Feed</h1>
            <p className="mt-1 text-sm text-slate-200/85 max-w-2xl">Fresh drops from your circles. Tap in, react, and favorite the best takes.</p>
          </div>
          {/* Accent color picker removed, now only in Profile settings */}
        </header>


        <main className="mt-8 grid gap-6 md:grid-cols-[20rem_1fr] lg:grid-cols-[20rem_1fr_340px] xl:grid-cols-[20rem_1fr_380px]">

        <NavigationBar accent={accent} variant="inline" />
          <section className="space-y-5">
            {posts.map((post) => (
              <article
                key={post.id}
                className="relative overflow-hidden rounded-3xl border border-white/10 bg-white/5 shadow-[0_20px_70px_rgba(0,0,0,0.4)] backdrop-blur-2xl"
                style={{ boxShadow: `0 20px 70px rgba(0,0,0,0.45), 0 0 32px ${accent.glow}` }}
              >
                <div className="flex items-center gap-3 px-5 py-4">
                  <div className="h-11 w-11 rounded-full border border-white/15 bg-white/10 flex items-center justify-center text-white/90" style={{ boxShadow: `0 10px 25px ${accent.glow}` }}>
                    <Sparkles className="h-5 w-5" />
                  </div>
                  <div>
                  <p className="text-sm font-semibold text-white">{post.author}</p>
                  <p className="text-sm text-slate-200/85 leading-relaxed ">{post.handle}</p>
                  </div>
                </div>

                <div className="relative overflow-hidden">
                  <img src={post.image} alt={post.title} className="h-[360px] w-full object-cover" />
                  <div className="pointer-events-none absolute inset-0 bg-gradient-to-t from-black/45 via-black/15 to-transparent" />
                </div>

                <div className="space-y-3 px-5 py-4">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <p className="text-sm uppercase tracking-[0.2em] text-slate-300/80">{post.anime}</p>
                      <h3 className="text-lg font-semibold text-white">{post.title}</h3>
                    </div>
                    <button
                      type="button"
                      onClick={() => toggleReaction(post.id, 'isFaved')}
                      className={`rounded-full border px-3 py-2 text-sm font-semibold transition ${
                        post.isFaved ? 'border-white/30 bg-white/15 text-white' : 'border-white/10 bg-white/5 text-slate-200/80 hover:text-white'
                      }`}
                      style={post.isFaved ? { boxShadow: `0 10px 30px ${accent.glow}` } : undefined}
                    >
                      <Bookmark className="mb-0.5 inline h-4 w-4" />
                      <span className="ml-2">{post.isFaved ? 'Saved' : 'Save'}</span>
                    </button>
                  </div>
                  <p className="text-sm text-slate-200/85 leading-relaxed">{post.description}</p>
                  <div className="flex items-center gap-3 text-sm text-slate-200/85">
                    <button
                      type="button"
                      onClick={() => toggleReaction(post.id, 'likes')}
                      className="group inline-flex items-center gap-1 rounded-full border border-white/10 bg-white/5 px-3 py-1.5 font-semibold text-white/90 transition hover:border-white/20"
                    >
                      <ThumbsUp className="h-4 w-4" />
                      <span>{post.likes}</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => toggleReaction(post.id, 'dislikes')}
                      className="group inline-flex items-center gap-1 rounded-full border border-white/10 bg-white/5 px-3 py-1.5 font-semibold text-white/90 transition hover:border-white/20"
                    >
                      <ThumbsDown className="h-4 w-4" />
                      <span>{post.dislikes}</span>
                    </button>
                    <button
                      type="button"
                      className="group inline-flex items-center gap-1 rounded-full border border-white/10 bg-white/5 px-3 py-1.5 font-semibold text-white/90 transition hover:border-white/20"
                    >
                      <MessageCircle className="h-4 w-4" />
                      <span>{post.comments}</span>
                    </button>
                    <button
                      type="button"
                      className="group inline-flex items-center gap-1 rounded-full border border-white/10 bg-white/5 px-3 py-1.5 font-semibold text-white/90 transition hover:border-white/20"
                    >
                      <Heart className="h-4 w-4" />
                      <span>Favorite</span>
                    </button>
                  </div>
                </div>
              </article>
            ))}
          </section>

          <aside className="hidden lg:block space-y-4 rounded-3xl border border-white/10 bg-white/5 p-6 shadow-[0_20px_70px_rgba(0,0,0,0.4)] backdrop-blur-2xl" style={{ boxShadow: `0 20px 70px rgba(0,0,0,0.4), 0 0 24px ${accent.glow}` }}>
            <p className="text-xs uppercase tracking-[0.25em] text-slate-300/80">Quick Picks</p>
            <div className="space-y-3 text-sm text-white/85">
              <div className="rounded-2xl border border-white/10 bg-white/10 p-4">Fresh art drops, cosplay previews, and watch-party teasers land here soon.</div>
              <div className="rounded-2xl border border-white/10 bg-white/10 p-4">Feed is local dummy data now. Plug your API later to swap live posts.</div>
            </div>
          </aside>
        </main>
      </div>

      <NavigationBar accent={accent} variant="mobile" />

      <AnimatePresence mode="wait">
        {showAccentLoader && <LoadingScreen key="loader-accent" accent={accent} />}
      </AnimatePresence>
    </div>
  )
}

export default Home
