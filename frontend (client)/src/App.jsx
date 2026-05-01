import React, { useState, useEffect, useCallback } from 'react'
import { weatherApi } from './services/weatherApi'
import Header from './components/Header'
import SearchBar from './components/SearchBar'
import CurrentWeather from './components/CurrentWeather'
import ForecastSection from './components/ForecastSection'
import MapView from './components/MapView'
import VideoSection from './components/VideoSection'
import DateRangeForm from './components/DateRangeForm'
import WeatherHistory from './components/WeatherHistory'
import ExportSection from './components/ExportSection'
import AboutSection from './components/AboutSection'

export default function App() {
  const [currentWeather, setCurrentWeather] = useState(null)
  const [forecast, setForecast] = useState(null)
  const [mapInfo, setMapInfo] = useState(null)
  const [videoData, setVideoData] = useState(null)
  const [records, setRecords] = useState([])
  const [searchLoading, setSearchLoading] = useState(false)
  const [saveLoading, setSaveLoading] = useState(false)
  const [editLoading, setEditLoading] = useState(false)
  const [searchError, setSearchError] = useState('')
  const [saveError, setSaveError] = useState('')
  const [saveSuccess, setSaveSuccess] = useState('')

  const loadRecords = useCallback(async () => {
    try {
      const data = await weatherApi.getAllRecords()
      setRecords(data)
    } catch {
      // silent — records section shows empty state
    }
  }, [])

  useEffect(() => { loadRecords() }, [loadRecords])

  async function handleSearch(location) {
    setSearchError('')
    setSearchLoading(true)
    setCurrentWeather(null)
    setForecast(null)
    setMapInfo(null)
    setVideoData(null)

    try {
      const [weather, fore, map, videos] = await Promise.allSettled([
        weatherApi.getCurrentWeather(location),
        weatherApi.getForecast(location),
        weatherApi.getMapInfo(location),
        weatherApi.getVideos(location),
      ])

      if (weather.status === 'fulfilled') setCurrentWeather(weather.value)
      else setSearchError(weather.reason?.message || 'Location not found.')

      if (fore.status === 'fulfilled') setForecast(fore.value)
      if (map.status === 'fulfilled') setMapInfo(map.value)
      if (videos.status === 'fulfilled') setVideoData(videos.value)
    } finally {
      setSearchLoading(false)
    }
  }

  async function handleSaveRecord(location, startDate, endDate) {
    setSaveError('')
    setSaveSuccess('')
    setSaveLoading(true)
    try {
      await weatherApi.createRecord(location, startDate, endDate)
      setSaveSuccess(`✅ Record saved for ${location} (${startDate} → ${endDate})`)
      await loadRecords()
    } catch (err) {
      setSaveError(err.message || 'Failed to save record')
    } finally {
      setSaveLoading(false)
    }
  }

  async function handleDeleteRecord(id) {
    try {
      await weatherApi.deleteRecord(id)
      await loadRecords()
    } catch (err) {
      alert('Delete failed: ' + (err.message || 'Unknown error'))
    }
  }

  async function handleEditRecord(id, location, startDate, endDate) {
    setEditLoading(true)
    try {
      await weatherApi.updateRecord(id, location, startDate, endDate)
      await loadRecords()
    } catch (err) {
      alert('Update failed: ' + (err.message || 'Unknown error'))
    } finally {
      setEditLoading(false)
    }
  }

  const unit = 'C'

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Header />

      <main style={{ flex: 1, maxWidth: '1200px', margin: '0 auto', width: '100%', padding: '1.5rem 1rem', display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>

        {/* Search */}
        <SearchBar onSearch={handleSearch} onGeolocate={handleSearch} loading={searchLoading} />

        {/* Search error */}
        {searchError && (
          <div style={{ background: '#fef2f2', border: '1px solid #fecaca', color: '#dc2626', padding: '0.75rem 1rem', borderRadius: '10px', fontSize: '0.9rem' }}>
            ⚠️ {searchError}
          </div>
        )}

        {/* Current weather + forecast side-by-side on wider screens */}
        {(currentWeather || searchLoading) && (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '1.25rem' }}>
            {currentWeather && <CurrentWeather weather={currentWeather} />}
            {forecast && <ForecastSection forecast={forecast} unit={unit} />}
          </div>
        )}

        {/* Map */}
        {mapInfo && <MapView mapInfo={mapInfo} />}

        {/* Videos */}
        {videoData && <VideoSection videoData={videoData} />}

        {/* Divider */}
        <div style={{ borderTop: '2px dashed #e2e8f0', paddingTop: '0.5rem' }}>
          <h2 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#1e293b' }}>
            📦 Database Operations
          </h2>
          <p style={{ fontSize: '0.8rem', color: '#64748b' }}>
            Save, view, edit, delete, and export weather records.
          </p>
        </div>

        {/* Date range form */}
        <DateRangeForm onSubmit={handleSaveRecord} loading={saveLoading} />

        {saveSuccess && (
          <div style={{ background: '#f0fdf4', border: '1px solid #bbf7d0', color: '#15803d', padding: '0.75rem 1rem', borderRadius: '10px', fontSize: '0.9rem' }}>
            {saveSuccess}
          </div>
        )}
        {saveError && (
          <div style={{ background: '#fef2f2', border: '1px solid #fecaca', color: '#dc2626', padding: '0.75rem 1rem', borderRadius: '10px', fontSize: '0.9rem' }}>
            ⚠️ {saveError}
          </div>
        )}

        {/* Records table */}
        <WeatherHistory
          records={records}
          onDelete={handleDeleteRecord}
          onEdit={handleEditRecord}
          loading={editLoading}
        />

        {/* Export */}
        <ExportSection recordCount={records.length} />

        {/* About */}
        <AboutSection />
      </main>
    </div>
  )
}
