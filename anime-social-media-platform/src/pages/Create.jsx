import { useMemo } from 'react'
import accentOptions from '../theme/accentOptions.js'
import { getSavedAccentKey } from '../theme/accentStorage.js'
import NavigationBar from '../components/NavigationBar.jsx'

function Create() {
  const accentKey = getSavedAccentKey('crimson-night')
  const accent = useMemo(() => accentOptions.find((opt) => opt.key === accentKey)?.colors || accentOptions[0].colors, [accentKey])

  return (
    <div
      className="relative min-h-screen overflow-hidden text-slate-50"
      style={{ backgroundImage: `linear-gradient(135deg, ${accent.start}, ${accent.mid}, ${accent.end})` }}
    >
      <div
        className="pointer-events-none absolute inset-0"
        style={{
          backgroundImage: `radial-gradient(circle at 45% 20%, ${accent.glow}, transparent 38%), radial-gradient(circle at 78% 30%, rgba(255,255,255,0.06), transparent 30%), radial-gradient(circle at 55% 78%, rgba(255,255,255,0.04), transparent 33%)`,
          mixBlendMode: 'screen',
        }}
      />
      <div className="relative z-10 px-4 pb-20 pt-14 sm:px-8 lg:px-12 xl:px-16">
        <div className="md:grid md:grid-cols-[20rem_1fr] md:gap-8">
          <aside className="hidden md:block">
            <NavigationBar accent={accent} variant="inline" />
          </aside>
          <main className="min-w-0">
            <header className="flex flex-wrap items-center justify-between gap-4">
              <div>
                <p className="text-xs uppercase tracking-[0.25em] text-slate-300/80">ThatOtakuNetwork</p>
                <h1 className="mt-2 text-3xl font-semibold text-white">Create +</h1>
                <p className="mt-2 text-sm text-slate-200/85 max-w-2xl">Launch posts, watch parties, or events with a few taps.</p>
              </div>
            </header>

            <div className="mt-8 grid gap-4 md:grid-cols-2">
              <div className="rounded-3xl border border-white/10 bg-white/5 p-6 backdrop-blur-2xl shadow-[0_20px_60px_rgba(0,0,0,0.35)]">
                <h3 className="text-lg font-semibold text-white">New Post</h3>
                <p className="mt-1 text-sm text-slate-200/85">Share thoughts or reactions. Draft-saving coming soon.</p>
              </div>
              <div className="rounded-3xl border border-white/10 bg-white/5 p-6 backdrop-blur-2xl shadow-[0_20px_60px_rgba(0,0,0,0.35)]">
                <h3 className="text-lg font-semibold text-white">Watch Party</h3>
                <p className="mt-1 text-sm text-slate-200/85">Pick a title, drop a time, invite friends and groups.</p>
              </div>
              <div className="rounded-3xl border border-white/10 bg-white/5 p-6 backdrop-blur-2xl shadow-[0_20px_60px_rgba(0,0,0,0.35)]">
                <h3 className="text-lg font-semibold text-white">Event</h3>
                <p className="mt-1 text-sm text-slate-200/85">Schedule meetups, AMAs, or co-op sessions.</p>
              </div>
            </div>
          </main>
        </div>
      </div>
      <NavigationBar accent={accent} variant="mobile" />
    </div>
  )
}

export default Create
