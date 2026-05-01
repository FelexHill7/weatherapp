import React from 'react'
import { celsiusToFahrenheit, formatDate } from '../utils/weatherUtils'

export default function ForecastSection({ forecast, unit }) {
  if (!forecast || !forecast.days || forecast.days.length === 0) return null

  return (
    <div style={{ background: '#fff', borderRadius: '12px', padding: '1.25rem', boxShadow: '0 4px 6px -1px rgb(0 0 0/0.1)' }}>
      <h2 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '1rem', color: '#1e293b' }}>
        5-Day Forecast — {forecast.locationName}
      </h2>
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))',
        gap: '0.75rem',
      }}>
        {forecast.days.slice(0, 5).map((day, i) => (
          <ForecastCard key={day.date} day={day} unit={unit} isToday={i === 0} />
        ))}
      </div>
    </div>
  )
}

function ForecastCard({ day, unit = 'C', isToday }) {
  const hi = unit === 'C' ? day.tempMax : celsiusToFahrenheit(day.tempMax)
  const lo = unit === 'C' ? day.tempMin : celsiusToFahrenheit(day.tempMin)
  const sym = unit === 'C' ? '°C' : '°F'

  return (
    <div style={{
      background: isToday ? '#dbeafe' : '#f8fafc',
      border: isToday ? '2px solid #2563eb' : '1px solid #e2e8f0',
      borderRadius: '10px',
      padding: '0.75rem',
      textAlign: 'center',
    }}>
      <p style={{ fontSize: '0.75rem', fontWeight: 600, color: '#64748b' }}>
        {isToday ? 'Today' : formatDate(day.date)}
      </p>
      <p style={{ fontSize: '2rem', margin: '0.4rem 0' }}>{day.weatherEmoji}</p>
      <p style={{ fontSize: '0.7rem', color: '#64748b', marginBottom: '0.4rem' }}>{day.description}</p>
      <p style={{ fontSize: '1rem', fontWeight: 700, color: '#1e293b' }}>
        {Math.round(hi)}{sym}
      </p>
      <p style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
        {Math.round(lo)}{sym}
      </p>
      {day.precipitationProbability > 0 && (
        <p style={{ fontSize: '0.7rem', color: '#0ea5e9', marginTop: '0.25rem' }}>
          🌂 {day.precipitationProbability}%
        </p>
      )}
      {day.windSpeedMax > 0 && (
        <p style={{ fontSize: '0.7rem', color: '#94a3b8' }}>
          💨 {Math.round(day.windSpeedMax)} km/h
        </p>
      )}
    </div>
  )
}
