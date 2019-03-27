require 'uri'
require 'net/http'

http = Net::HTTP.new(ARGV[0],80)
req = Net::HTTP::Get.new(ARGV[1], {'User-Agent' => 'windows 7:weather_data_scraper:v0.1'})
response = http.request req

puts response.body