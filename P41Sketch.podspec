
Pod::Spec.new do |s|
  s.name         = "P41Sketch"
  s.version      = "1.0.0"
  s.summary      = "P41Sketch"
  s.description  = <<-DESC
                  P41Sketch
                   DESC
  s.homepage     = "https://github.com/csae0/react-native-sketch-view.git"
  s.license      = "MIT"
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/csae0/react-native-sketch-view.git", :branch => "master" }
  s.source_files  = "ios/**/*.{h,m}"
  s.requires_arc = true

  s.dependency "React"
end

  
